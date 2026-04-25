package com.example.desabackend.service;

import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpResponseDto;
import com.example.desabackend.dto.PasswordResetConfirmDto;
import com.example.desabackend.entity.OtpEntity;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.EmailDeliveryException;
import com.example.desabackend.exception.UnauthorizedException;
import com.example.desabackend.repository.OtpRepository;
import com.example.desabackend.repository.UserRepository;
import com.example.desabackend.services.interfaces.IOtpService;
import com.example.desabackend.util.EmailUtils;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class OtpServiceImpl implements IOtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);
    private static final int OTP_EXPIRATION_MINUTES = 10;
    private static final int MAX_OTP_ATTEMPTS = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final LoginResponseBuilder loginResponseBuilder;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.from:noreply@xplorenow.com}")
    private String mailFrom;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${mail.fail-fast:true}")
    private boolean mailFailFast;

    public OtpServiceImpl(
            OtpRepository otpRepository,
            UserRepository userRepository,
            JavaMailSender mailSender,
            LoginResponseBuilder loginResponseBuilder,
            PasswordEncoder passwordEncoder
    ) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.loginResponseBuilder = loginResponseBuilder;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OtpResponseDto requestSignupOtp(String email) {
        String normalizedEmail = EmailUtils.normalize(email);
        requirePendingUser(normalizedEmail);
        return createOtp(
                normalizedEmail,
                "Código OTP enviado al email. Válido por " + OTP_EXPIRATION_MINUTES + " minutos."
        );
    }

    @Override
    @Transactional
    public OtpResponseDto resendSignupOtp(String email) {
        String normalizedEmail = EmailUtils.normalize(email);
        // No se verifica el estado del usuario: funciona tanto para registro (pendiente)
        // como para login OTP (activo). Solo se requiere que haya un OTP no verificado.
        if (!userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("No existe una cuenta con ese email.");
        }
        return resendOtpInternal(
                normalizedEmail,
                "No hay un OTP pendiente para este email. Solicitá uno nuevo.",
                "Nuevo código OTP reenviado. Válido por " + OTP_EXPIRATION_MINUTES + " minutos."
        );
    }

    @Override
    @Transactional
    public LoginResponseDto verifySignupOtpCode(String email, String code) {
        String normalizedEmail = EmailUtils.normalize(email);
        UserEntity user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con ese email."));

        OtpEntity otp = getActiveOtp(normalizedEmail);
        validateOtpCode(otp, code);
        markOtpVerified(otp);

        if (Boolean.FALSE.equals(user.getEnabled())) {
            user.setEnabled(true);
            userRepository.save(user);
            logger.info("User {} activated after OTP verification.", normalizedEmail);
        }

        return loginResponseBuilder.build(user);
    }

    @Override
    @Transactional
    public LoginResponseDto verifyLoginOtp(String email, String code) {
        String normalizedEmail = EmailUtils.normalize(email);
        UserEntity user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("No existe una cuenta con ese email."));

        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new IllegalArgumentException(
                    "Tu cuenta aún no está verificada. Completá el registro primero.");
        }

        OtpEntity otp = getActiveOtp(normalizedEmail);
        validateOtpCode(otp, code);
        markOtpVerified(otp);

        return loginResponseBuilder.build(user);
    }

    @Override
    @Transactional
    public OtpResponseDto sendLoginOtp(String email) {
        String normalizedEmail = EmailUtils.normalize(email);
        UserEntity user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una cuenta registrada con ese email."));
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new IllegalArgumentException(
                    "Tu cuenta aún no está verificada. Completá el registro primero.");
        }
        return createOtp(
                normalizedEmail,
                "Código de acceso enviado. Válido por " + OTP_EXPIRATION_MINUTES + " minutos."
        );
    }

    @Override
    @Transactional
    public OtpResponseDto requestPasswordReset(String email) {
        String normalizedEmail = EmailUtils.normalize(email);
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
    @Transactional
    public OtpResponseDto verifyPasswordResetCode(String email, String code) {
        String normalizedEmail = EmailUtils.normalize(email);
        requireExistingUser(normalizedEmail);
        OtpEntity otp = getActiveOtp(normalizedEmail);
        validateOtpCode(otp, code);
        return new OtpResponseDto("Código validado correctamente.", normalizedEmail);
    }

    @Override
    @Transactional
    public LoginResponseDto confirmPasswordReset(PasswordResetConfirmDto request) {
        String normalizedEmail = EmailUtils.normalize(request.email());
        UserEntity user = requireExistingUser(normalizedEmail);

        OtpEntity otp = getActiveOtp(normalizedEmail);
        validateOtpCode(otp, request.code());

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        if (Boolean.FALSE.equals(user.getEnabled())) {
            user.setEnabled(true);
        }
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
        boolean emailSent = sendOtpEmail(normalizedEmail, code);
        if (!emailSent) {
            return new OtpResponseDto(
                    "Generamos tu código OTP, pero no se pudo enviar el email. "
                            + "Revisá MailHog o la configuración SMTP y volvé a intentar.",
                    normalizedEmail
            );
        }
        return new OtpResponseDto(successMessage, normalizedEmail);
    }

    private OtpResponseDto resendOtpInternal(
            String normalizedEmail, String notFoundMessage, String successMessage) {
        OtpEntity lastOtp = otpRepository
                .findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException(notFoundMessage));

        String newCode = generateOtpCode();
        lastOtp.setCode(newCode);
        lastOtp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES));
        lastOtp.setAttemptCount(0);
        otpRepository.save(lastOtp);
        boolean emailSent = sendOtpEmail(normalizedEmail, newCode);

        if (!emailSent) {
            return new OtpResponseDto(
                    "Generamos un nuevo código OTP, pero no se pudo enviar el email. "
                            + "Revisá MailHog o la configuración SMTP y volvé a intentar.",
                    normalizedEmail
            );
        }

        return new OtpResponseDto(successMessage, normalizedEmail);
    }

    private OtpEntity getActiveOtp(String normalizedEmail) {
        return otpRepository
                .findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        normalizedEmail, LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay un código OTP activo o válido para este email. Solicitá uno nuevo."));
    }

    private void validateOtpCode(OtpEntity otp, String code) {
        if (otp.getAttemptCount() >= MAX_OTP_ATTEMPTS) {
            throw new IllegalStateException(
                    "Demasiados intentos fallidos. Solicitá un nuevo código OTP.");
        }
        if (otp.isExpired()) {
            throw new IllegalArgumentException("El código OTP ha expirado. Solicitá uno nuevo.");
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

    private void requirePendingUser(String normalizedEmail) {
        userRepository.findByEmailIgnoreCase(normalizedEmail).ifPresent(user -> {
            if (Boolean.TRUE.equals(user.getEnabled())) {
                throw new IllegalArgumentException(
                        "Este email ya está registrado. Usá login o recuperá tu contraseña.");
            }
        });
    }

    private UserEntity requireExistingUser(String normalizedEmail) {
        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una cuenta registrada con ese email."));
    }

    private boolean sendOtpEmail(String email, String code) {
        try {
            logger.info("Preparing to send OTP email to: {}", email);
            String resolvedFrom = resolveMailFrom();
            logger.debug("Mail from: {}, Mail host settings configured", resolvedFrom);

            SimpleMailMessage message = new SimpleMailMessage();
            if (StringUtils.hasText(resolvedFrom)) {
                message.setFrom(resolvedFrom);
            }
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
            return true;

        } catch (MailException e) {
            Throwable rootCause = e.getMostSpecificCause();
            logger.error(
                    "Failed to send OTP email to {}. Cause: {}",
                    email,
                    rootCause != null ? rootCause.getMessage() : e.getMessage(),
                    e
            );
            if (mailFailFast) {
                throw new EmailDeliveryException("No se pudo enviar el email OTP", e);
            }
            return false;
        }
    }

    private String resolveMailFrom() {
        if (StringUtils.hasText(mailFrom)) {
            return mailFrom.trim();
        }
        if (StringUtils.hasText(mailUsername)) {
            return mailUsername.trim();
        }
        return null;
    }
}