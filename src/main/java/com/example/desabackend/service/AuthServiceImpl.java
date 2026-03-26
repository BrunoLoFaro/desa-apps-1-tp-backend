package com.example.desabackend.service;

import com.example.desabackend.dto.LoginRequestDto;
import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.RegisterRequestDto;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.UnauthorizedException;
import com.example.desabackend.repository.UserRepository;
import java.time.LocalDateTime;
import com.example.desabackend.services.interfaces.IAuthService;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {
        UserEntity user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new UnauthorizedException("User account is disabled");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtTokenProvider.generateToken(user.getId().toString());

        return new LoginResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getDni(),
                token
        );
    }

    @Override
    @Transactional
    public LoginResponseDto register(RegisterRequestDto request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }

        String normalizedDni = normalizeDni(request.dni());
        if (userRepository.existsByDni(normalizedDni)) {
            throw new IllegalArgumentException("DNI already registered");
        }

        UserEntity user = new UserEntity();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setDni(normalizedDni);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        UserEntity saved = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(saved.getId().toString());

        return new LoginResponseDto(
                saved.getId(),
                saved.getEmail(),
                saved.getFirstName(),
                saved.getLastName(),
                saved.getDni(),
                token
        );
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeDni(String dni) {
        return dni == null ? "" : dni.trim();
    }
}
