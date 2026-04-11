package com.example.desabackend.service;

import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpRegistrationCompleteDto;
import com.example.desabackend.dto.OtpResponseDto;
import com.example.desabackend.dto.PasswordResetConfirmDto;
import com.example.desabackend.dto.RegisterRequestDto;
import com.example.desabackend.entity.OtpEntity;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.UnauthorizedException;
import com.example.desabackend.repository.OtpRepository;
import com.example.desabackend.repository.UserRepository;
import com.example.desabackend.services.interfaces.IAuthService;
import com.example.desabackend.services.interfaces.IOtpService;
import com.example.desabackend.util.EmailUtils;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OtpServiceImpl implements IOtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);
    private static final int OTP_EXPIRATION_MINUTES = 10;
    private static final int MAX_OTP_ATTEMPTS = 5;
    // FIX: SecureRandom en lugar de Random (java.util.Random es predecible)
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final LoginResponseBuilder loginResponseBuilder;
    private final IAuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    @Value("${spring.mail.from:noreply@xplorenow.com}")
    private String mailFrom;

    public OtpServiceImpl(
            OtpRepository otpRepository,
            UserRepository userRepository,
            JavaMailSender mailSender,
            LoginResponseBuilder loginResponseBuilder,
            IAuthService authService,
            PasswordEncoder passwordEncoder,
            Environment env
    ) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.loginResponseBuilder = loginResponseBuilder;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.env = env;
    }

    @Override
    @Transactional
    public OtpResponseDto requestSignupOtp(String email) {
        String normalizedEmail = EmailUtils.normalize(email);
        ensureEmailIsAvailable(normalizedEmail);
        return createOtp(normalizedEmail,
                "Código OTP enviado al email. Válido por " + OTP_EXPIRATION_MINUTES + " minutos.");
    }

    @Override
    @Transactional
    public OtpResponseDto resendSignupOtp(String email) {
        String normalizedEmail = EmailUtils.normalize(email);
        ensureEmailIsAvailable(normalizedEmail);
        return resendOtpInternal(
                normalizedEmail,
                "No hay un OTP pendiente para este email. Solicita uno nuevo primero.",
                "Nuevo código OTP reenviado. Válido por " + OTP_EXPIRATION_MINUTES + " minutos."
        );
    }

    @Override
    @Transactional
    public OtpResponseDto verifySignupOtpCode(String email, String code) {
        String normalizedEmail = EmailUtils.normalize(email);
        ensureEmailIsAvailable(normalizedEmail);
        OtpEntity otp = getActiveOtp(normalizedEmail);
        validateOtpCode(otp, code);
        return new OtpResponseDto("Código válido.", normalizedEmail);
    }

    @Override
    @Transactional
    public LoginResponseDto completeSignupWithOtp(OtpRegistrationCompleteDto request) {
        String normalizedEmail = EmailUtils.normalize(request.email());
        ensureEmailIsAvailable(normalizedEmail);

        OtpEntity otp = getActiveOtp(normalizedEmail);
        validateOtpCode(otp, request.code());

        LoginResponseDto response = authService.register(new RegisterRequestDto(
                normalizedEmail,
                request.password(),
                request.firstName(),
                request.lastName(),
                request.dni()
        ));

        markOtpVerified(otp);
        return response;
    }

    @Override
    @Transactional
    public OtpResponseDto requestPasswordReset(String email) {
        String normalizedEmail = EmailUtils.normalize(email);
        // FIX: respuesta genérica para no revelar si el email existe (user enumeration)
        String successMessage =
                "Si existe una cuenta con este email, recibirás un código de recuperación.";
        if (!userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return new OtpResponseDto(successMessage, normalizedEmail);
        }
        return createOtp(normalizedEmail, successMessage);
    }

    @Override
    @Transactional
    public OtpResponseDto resendPasswordReset(String email) {
        String normalizedEmail = EmailUtils.normalize(email);
        requireExistingUser(normalizedEmail);
        return resendOtpInternal(
                normalizedEmail,
                "No hay un OTP pendiente para este email. Solicita uno nuevo primero.",
                "Nuevo código de restablecimiento reenviado. Válido por " + OTP_EXPIRATION_MINUTES + " minutos."
        );
    }

    @Override
    @Transactional  // FIX: era @Transactional(readOnly=true) pero validateOtpCode escribe attemptCount
    public OtpResponseDto verifyPasswordResetCode(String email, String code) {
        String normalizedEmail = EmailUtils.normalize(email);
        requireExistingUser(normalizedEmail);
        OtpEntity otp = getActiveOtp(normalizedEmail);
        validateOtpCode(otp, code);
        return new OtpResponseDto("Código validado correctamente.", normalizedEmail);
    }

    @Override
    @Transactional  // FIX: faltaba @Transactional — sin esto, un fallo parcial deja estado inconsistente
    public LoginResponseDto confirmPasswordReset(PasswordResetConfirmDto request) {
        String normalizedEmail = EmailUtils.normalize(request.email());
        UserEntity user = requireExistingUser(normalizedEmail);

        OtpEntity otp = getActiveOtp(normalizedEmail);
        validateOtpCode(otp, request.code());

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        UserEntity savedUser = userRepository.save(user);
        markOtpVerified(otp);

        return loginResponseBuilder.build(savedUser);
    }

    // ── helpers privados ──────────────────────────────────────────────────────

    private String generateOtpCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    private OtpResponseDto createOtp(String normalizedEmail, String successMessage) {
        logger.info("Creating OTP for email: {}", normalizedEmail);
        String code = generateOtpCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);
        OtpEntity otpEntity = new OtpEntity(normalizedEmail, code, expiresAt);
        otpRepository.save(otpEntity);
        logger.info("OTP created successfully, sending email to: {}", normalizedEmail);
        sendOtpEmail(normalizedEmail, code);
        return new OtpResponseDto(successMessage, normalizedEmail);
    }

    private OtpResponseDto resendOtpInternal(
            String normalizedEmail, String notFoundMessage, String successMessage) {
        // FIX: solo buscar OTPs no verificados (antes usaba findTopByEmailOrderByCreatedAtDesc
        //      que podía encontrar un OTP ya verificado)
        OtpEntity lastOtp = otpRepository
                .findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException(notFoundMessage));

        String newCode = generateOtpCode();
        lastOtp.setCode(newCode);
        lastOtp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
        lastOtp.setAttemptCount(0);
        otpRepository.save(lastOtp);
        sendOtpEmail(normalizedEmail, newCode);

        return new OtpResponseDto(successMessage, normalizedEmail);
    }

    private OtpEntity getActiveOtp(String normalizedEmail) {
        return otpRepository
                .findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        normalizedEmail, LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay un código OTP activo o válido para este email. Solicita uno nuevo."));
    }

    private void validateOtpCode(OtpEntity otp, String code) {
        if (otp.getAttemptCount() >= MAX_OTP_ATTEMPTS) {
            throw new IllegalStateException(
                    "Demasiados intentos fallidos. Solicita un nuevo código OTP.");
        }
        if (otp.isExpired()) {
            throw new IllegalArgumentException("El código OTP ha expirado. Solicita uno nuevo.");
        }
        if (!otp.getCode().equals(code)) {
            otp.setAttemptCount(otp.getAttemptCount() + 1);
            otpRepository.save(otp);
            throw new UnauthorizedException("Código OTP inválido.");
        }
    }

    private void markOtpVerified(OtpEntity otp) {
        otp.setVerified(true);
        otpRepository.save(otp);
    }

    private void ensureEmailIsAvailable(String normalizedEmail) {
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "Este email ya está registrado. Usá login o recuperá tu contraseña.");
        }
    }

    private UserEntity requireExistingUser(String normalizedEmail) {
        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una cuenta registrada con ese email."));
    }

    private void sendOtpEmail(String email, String code) {
        try {
            logger.info("Preparing to send OTP email to: {}", email);
            logger.debug("Mail from: {}, Mail host settings configured", mailFrom);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(email);
            message.setSubject("Tu código de acceso - XploreNow");
            message.setText(
                    "Tu código OTP es: " + code + "\n\n"
                    + "Este código expirará en " + OTP_EXPIRATION_MINUTES + " minutos.\n\n"
                    + "Si no solicitaste este código, ignorá este mensaje.\n\n"
                    + "XploreNow Team"
            );
            
            logger.info("Sending email with OTP code to: {}", email);
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", email);
            
        } catch (Exception e) {
            logger.error("Failed to send OTP email to {}: {}", email, e.getMessage(), e);
            
            // Development fallback: log the OTP code
            if (isDevelopmentEnvironment()) {
                logger.warn("DEVELOPMENT MODE - OTP code for {}: {}", email, code);
            }
            
            // Don't throw exception - OTP flow should continue
            logger.info("OTP generation completed successfully despite email failure");
        }
    }
    
    private boolean isDevelopmentEnvironment() {
        String[] activeProfiles = env.getActiveProfiles();
        for (String profile : activeProfiles) {
            if (profile.equalsIgnoreCase("dev") || profile.equalsIgnoreCase("development")) {
                return true;
            }
        }
        // Also check if we're not in production
        return !Arrays.asList(activeProfiles).contains("prod");
    }

}
