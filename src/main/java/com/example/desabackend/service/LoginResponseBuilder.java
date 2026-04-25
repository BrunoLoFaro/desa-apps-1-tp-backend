package com.example.desabackend.service;

import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Construye un LoginResponseDto a partir de un UserEntity + token JWT.
 *
 * Centraliza la lógica duplicada entre AuthServiceImpl y OtpServiceImpl,
 * que ambos necesitaban generar token + mapear campos del usuario.
 */
@Component
public class LoginResponseBuilder {

    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponseBuilder(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponseDto build(UserEntity user) {
        String token = jwtTokenProvider.generateToken(user.getId().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId().toString());
        return new LoginResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                token,
                refreshToken
        );
    }
}
