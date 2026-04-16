package com.example.desabackend.services.interfaces;

import com.example.desabackend.dto.LoginRequestDto;
import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.RefreshRequestDto;
import com.example.desabackend.dto.RefreshResponseDto;
import com.example.desabackend.dto.RegisterRequestDto;

public interface IAuthService {

    LoginResponseDto login(LoginRequestDto request);

    LoginResponseDto register(RegisterRequestDto request);

    RefreshResponseDto refresh(RefreshRequestDto request);
}
