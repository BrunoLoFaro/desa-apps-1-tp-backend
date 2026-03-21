package com.example.desabackend.services.interfaces;

import com.example.desabackend.dto.AccountRecoveryConfirmRequestDto;
import com.example.desabackend.dto.AccountRecoveryRequestDto;
import com.example.desabackend.dto.LoginRequestDto;
import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpEmailRequestDto;
import com.example.desabackend.dto.OtpSignupConfirmRequestDto;
import com.example.desabackend.dto.RegisterRequestDto;

public interface IAuthService {

    LoginResponseDto register(RegisterRequestDto request);

    LoginResponseDto login(LoginRequestDto request);

    void requestSignupOtp(OtpEmailRequestDto request);

    void resendSignupOtp(OtpEmailRequestDto request);

    LoginResponseDto confirmSignupOtp(OtpSignupConfirmRequestDto request);

    void requestAccountRecovery(AccountRecoveryRequestDto request);

    void resendAccountRecoveryOtp(AccountRecoveryRequestDto request);

    void confirmAccountRecovery(AccountRecoveryConfirmRequestDto request);
}
