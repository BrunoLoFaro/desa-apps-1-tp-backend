package com.example.desabackend.services.interfaces;

import com.example.desabackend.dto.LoginRequestDto;
import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.RegisterRequestDto;

public interface IAuthService {

    LoginResponseDto login(LoginRequestDto request);

    LoginResponseDto register(RegisterRequestDto request);
}
