package com.example.desabackend.service;

import com.example.desabackend.dto.AccountRecoveryConfirmRequestDto;
import com.example.desabackend.dto.AccountRecoveryRequestDto;
import com.example.desabackend.dto.LoginRequestDto;
import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpEmailRequestDto;
import com.example.desabackend.dto.OtpSignupConfirmRequestDto;
import com.example.desabackend.dto.RegisterRequestDto;
import com.example.desabackend.entity.AccountRecoveryOtpEntity;
import com.example.desabackend.entity.OtpPurpose;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.ConflictException;
import com.example.desabackend.exception.UnauthorizedException;
import com.example.desabackend.repository.AccountRecoveryOtpRepository;
import com.example.desabackend.repository.UserRepository;
import com.example.desabackend.services.interfaces.IAuthService;
import com.example.desabackend.services.interfaces.IRecoveryOtpNotifier;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements IAuthService {

    private static final int OTP_EXPIRATION_MINUTES = 10;

    private final UserRepository userRepository;
    private final AccountRecoveryOtpRepository recoveryOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRecoveryOtpNotifier recoveryOtpNotifier;

    public AuthServiceImpl(
            UserRepository userRepository,
            AccountRecoveryOtpRepository recoveryOtpRepository,
            PasswordEncoder passwordEncoder,
            IRecoveryOtpNotifier recoveryOtpNotifier
    ) {
        this.userRepository = userRepository;
        this.recoveryOtpRepository = recoveryOtpRepository;
        this.passwordEncoder = passwordEncoder;
        this.recoveryOtpNotifier = recoveryOtpNotifier;
    }

    @Override
    @Transactional
    public LoginResponseDto register(RegisterRequestDto request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("Email already registered");
        }

        UserEntity user = new UserEntity();
        user.setEmail(normalizedEmail);
        user.setDisplayName(request.displayName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        UserEntity saved = userRepository.save(user);
        return new LoginResponseDto(saved.getId(), saved.getEmail(), saved.getDisplayName());
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

        return new LoginResponseDto(user.getId(), user.getEmail(), user.getDisplayName());
    }

    @Override
    @Transactional
    public void requestSignupOtp(OtpEmailRequestDto request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("Email already registered");
        }
        issueOtp(normalizedEmail, OtpPurpose.SIGNUP, "alta de cuenta");
    }

    @Override
    @Transactional
    public void resendSignupOtp(OtpEmailRequestDto request) {
        requestSignupOtp(request);
    }

    @Override
    @Transactional
    public LoginResponseDto confirmSignupOtp(OtpSignupConfirmRequestDto request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("Email already registered");
        }

        AccountRecoveryOtpEntity otpRecord = findValidOtpOrThrow(normalizedEmail, OtpPurpose.SIGNUP, request.otp());

        UserEntity user = new UserEntity();
        user.setEmail(normalizedEmail);
        user.setDisplayName(request.displayName().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        UserEntity saved = userRepository.save(user);

        otpRecord.setUsed(true);
        recoveryOtpRepository.save(otpRecord);

        return new LoginResponseDto(saved.getId(), saved.getEmail(), saved.getDisplayName());
    }

    @Override
    @Transactional
    public void requestAccountRecovery(AccountRecoveryRequestDto request) {
        String normalizedEmail = normalizeEmail(request.email());
        userRepository.findByEmailIgnoreCase(normalizedEmail)
                .filter(user -> !Boolean.FALSE.equals(user.getEnabled()))
                .ifPresent(user -> issueOtp(normalizedEmail, OtpPurpose.RECOVERY, "recuperacion de cuenta"));
    }

    @Override
    @Transactional
    public void resendAccountRecoveryOtp(AccountRecoveryRequestDto request) {
        requestAccountRecovery(request);
    }

    @Override
    @Transactional
    public void confirmAccountRecovery(AccountRecoveryConfirmRequestDto request) {
        String normalizedEmail = normalizeEmail(request.email());
        AccountRecoveryOtpEntity recovery = findValidOtpOrThrow(normalizedEmail, OtpPurpose.RECOVERY, request.otp());

        UserEntity user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired OTP"));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        recovery.setUsed(true);
        recoveryOtpRepository.save(recovery);
    }

    private void issueOtp(String normalizedEmail, OtpPurpose purpose, String contextLabel) {
        String otp = generateOtp();

        AccountRecoveryOtpEntity otpRecord = new AccountRecoveryOtpEntity();
        otpRecord.setEmail(normalizedEmail);
        otpRecord.setPurpose(purpose);
        otpRecord.setOtpHash(hashText(otp));
        otpRecord.setCreatedAt(LocalDateTime.now());
        otpRecord.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
        otpRecord.setUsed(false);
        recoveryOtpRepository.save(otpRecord);

        recoveryOtpNotifier.sendOtp(normalizedEmail, otp, OTP_EXPIRATION_MINUTES, contextLabel);
    }

    private AccountRecoveryOtpEntity findValidOtpOrThrow(String normalizedEmail, OtpPurpose purpose, String otp) {
        AccountRecoveryOtpEntity otpRecord = recoveryOtpRepository
                .findTopByEmailAndPurposeOrderByCreatedAtDesc(normalizedEmail, purpose)
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired OTP"));

        boolean expired = otpRecord.getExpiresAt() == null || otpRecord.getExpiresAt().isBefore(LocalDateTime.now());
        if (Boolean.TRUE.equals(otpRecord.getUsed()) || expired) {
            throw new UnauthorizedException("Invalid or expired OTP");
        }

        String providedHash = hashText(otp);
        if (!providedHash.equals(otpRecord.getOtpHash())) {
            throw new UnauthorizedException("Invalid or expired OTP");
        }

        return otpRecord;
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private static String generateOtp() {
        int value = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format("%06d", value);
    }

    private static String hashText(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hashBytes.length * 2);
            for (byte hashByte : hashBytes) {
                sb.append(String.format("%02x", hashByte));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
