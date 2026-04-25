package com.example.desabackend.service;

import com.example.desabackend.dto.LoginRequestDto;
import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpResponseDto;
import com.example.desabackend.dto.RefreshRequestDto;
import com.example.desabackend.dto.RefreshResponseDto;
import com.example.desabackend.dto.RegisterRequestDto;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.UnauthorizedException;
import com.example.desabackend.repository.UserRepository;
import java.util.Optional;
import com.example.desabackend.services.interfaces.IAuthService;
import com.example.desabackend.services.interfaces.IOtpService;
import com.example.desabackend.util.EmailUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements IAuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginResponseBuilder loginResponseBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final IOtpService otpService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            LoginResponseBuilder loginResponseBuilder,
            JwtTokenProvider jwtTokenProvider,
            IOtpService otpService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginResponseBuilder = loginResponseBuilder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpService = otpService;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {
        log.info("Login attempt for email: {}", EmailUtils.normalize(request.email()));
        try {
            UserEntity user = userRepository.findByEmailIgnoreCase(EmailUtils.normalize(request.email()))
                    .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas."));

            if (Boolean.FALSE.equals(user.getEnabled())) {
                throw new UnauthorizedException("Tu cuenta aún no está verificada. Revisá tu correo para completar el registro.");
            }

            if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
                throw new UnauthorizedException("Credenciales inválidas.");
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
    public OtpResponseDto register(RegisterRequestDto request) {
        String normalizedEmail = EmailUtils.normalize(request.email());
        log.info("Registration attempt for email: {}", normalizedEmail);

        Optional<UserEntity> existing = userRepository.findByEmailIgnoreCase(normalizedEmail);
        if (existing.isPresent()) {
            UserEntity existingUser = existing.get();
            if (Boolean.TRUE.equals(existingUser.getEnabled())) {
                log.warn("Registration failed: email {} already active", normalizedEmail);
                throw new IllegalArgumentException("Este email ya está registrado. Usá login o recuperá tu contraseña.");
            }
            // Usuario pendiente de verificación: actualiza datos y reenvía OTP
            log.info("Pending user found for {}, updating and resending OTP.", normalizedEmail);
            existingUser.setPasswordHash(passwordEncoder.encode(request.password()));
            existingUser.setFirstName(request.firstName().trim());
            existingUser.setLastName(request.lastName().trim());
            if (StringUtils.hasText(request.phone())) {
                existingUser.setPhone(request.phone().trim());
            }
            userRepository.save(existingUser);
            return otpService.requestSignupOtp(normalizedEmail);
        }

        UserEntity user = new UserEntity();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        if (StringUtils.hasText(request.phone())) {
            user.setPhone(request.phone().trim());
        }
        user.setEnabled(false);
        userRepository.save(user);
        log.info("Pending user created for email: {}", normalizedEmail);

        return otpService.requestSignupOtp(normalizedEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshResponseDto refresh(RefreshRequestDto request) {
        if (!jwtTokenProvider.validateRefreshToken(request.refreshToken())) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }
        String userId = jwtTokenProvider.getUserIdFromToken(request.refreshToken());
        UserEntity user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new UnauthorizedException("User account is disabled");
        }
        String newAccessToken = jwtTokenProvider.generateToken(userId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
        log.info("Token refreshed for user: {}", userId);
        return new RefreshResponseDto(newAccessToken, newRefreshToken);
    }
}