package com.example.desabackend.services.interfaces;

import com.example.desabackend.dto.LoginRequestDto;
import com.example.desabackend.dto.LoginResponseDto;

public interface IAuthService {

    LoginResponseDto login(LoginRequestDto request);
}
