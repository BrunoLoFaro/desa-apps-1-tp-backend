package com.example.desabackend.service;

import com.example.desabackend.dto.LoginRequestDto;
import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.RegisterRequestDto;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.UnauthorizedException;
import com.example.desabackend.repository.UserRepository;
import com.example.desabackend.util.EmailUtils;
import com.example.desabackend.services.interfaces.IAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements IAuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginResponseBuilder loginResponseBuilder;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            LoginResponseBuilder loginResponseBuilder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginResponseBuilder = loginResponseBuilder;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {
        log.info("Login attempt for email: {}", EmailUtils.normalize(request.email()));
        try {
            UserEntity user = userRepository.findByEmailIgnoreCase(EmailUtils.normalize(request.email()))
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

            if (Boolean.FALSE.equals(user.getEnabled())) {
                throw new UnauthorizedException("User account is disabled");
            }

            if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                throw new UnauthorizedException("Invalid credentials");
            }

            log.info("Login successful for user: {}", user.getId());
            return loginResponseBuilder.build(user);
        } catch (UnauthorizedException ex) {
            log.warn("Login failed for email {}: {}", request.email(), ex.getMessage());
            throw ex;
        }
    }

    @Override
    @Transactional
    public LoginResponseDto register(RegisterRequestDto request) {
        String normalizedEmail = EmailUtils.normalize(request.email());
        log.info("Registration attempt for email: {}", normalizedEmail);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            log.warn("Registration failed: email {} already exists", normalizedEmail);
            throw new IllegalArgumentException("Email already registered");
        }

        String normalizedDni = normalizeDni(request.dni());
        if (userRepository.existsByDni(normalizedDni)) {
            log.warn("Registration failed: DNI {} already exists", normalizedDni);
            throw new IllegalArgumentException("DNI already registered");
        }

        UserEntity user = new UserEntity();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setDni(normalizedDni);
        UserEntity saved = userRepository.save(user);
        log.info("Registration successful for user: {}", saved.getId());
        return loginResponseBuilder.build(saved);
    }

    private static String normalizeDni(String dni) {
        return dni == null ? "" : dni.trim();
    }
}
