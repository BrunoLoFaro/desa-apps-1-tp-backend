package com.example.desabackend.controller;

import com.example.desabackend.dto.AccountRecoveryConfirmRequestDto;
import com.example.desabackend.dto.AccountRecoveryRequestDto;
import com.example.desabackend.dto.LoginRequestDto;
import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpEmailRequestDto;
import com.example.desabackend.dto.OtpSignupConfirmRequestDto;
import com.example.desabackend.dto.RegisterRequestDto;
import com.example.desabackend.services.interfaces.IAuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public LoginResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

    @PostMapping("/otp/request")
    public ResponseEntity<Void> requestSignupOtp(@Valid @RequestBody OtpEmailRequestDto request) {
        authService.requestSignupOtp(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/otp/resend")
    public ResponseEntity<Void> resendSignupOtp(@Valid @RequestBody OtpEmailRequestDto request) {
        authService.resendSignupOtp(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/otp/confirm")
    public LoginResponseDto confirmSignupOtp(@Valid @RequestBody OtpSignupConfirmRequestDto request) {
        return authService.confirmSignupOtp(request);
    }

    @PostMapping("/recovery/request")
    public ResponseEntity<Void> requestRecovery(@Valid @RequestBody AccountRecoveryRequestDto request) {
        authService.requestAccountRecovery(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/recovery/resend")
    public ResponseEntity<Void> resendRecovery(@Valid @RequestBody AccountRecoveryRequestDto request) {
        authService.resendAccountRecoveryOtp(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/recovery/confirm")
    public ResponseEntity<Void> confirmRecovery(@Valid @RequestBody AccountRecoveryConfirmRequestDto request) {
        authService.confirmAccountRecovery(request);
        return ResponseEntity.noContent().build();
    }
}
