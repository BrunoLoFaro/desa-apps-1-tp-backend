package com.example.desabackend.service;

import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpResponseDto;
import com.example.desabackend.entity.OtpEntity;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.UnauthorizedException;
import com.example.desabackend.repository.OtpRepository;
import com.example.desabackend.repository.UserRepository;
import com.example.desabackend.services.interfaces.IOtpService;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OtpServiceImpl implements IOtpService {

    private static final int OTP_EXPIRATION_MINUTES = 10;
    private static final int MAX_OTP_ATTEMPTS = 5;
    private static final Random RANDOM = new Random();

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.mail.from:noreply@xplorenow.com}")
    private String mailFrom;

    public OtpServiceImpl(
            OtpRepository otpRepository,
            UserRepository userRepository,
            JavaMailSender mailSender,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public OtpResponseDto requestOtp(String email) {
        String normalizedEmail = normalizeEmail(email);

        // Verificar que el email no esté vinculado a una cuenta existente
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Este email ya está registrado. Usa login en su lugar.");
        }

        // Generar código OTP de 6 dígitos
        String code = generateOtpCode();

        // Crear entidad OTP
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);
        OtpEntity otpEntity = new OtpEntity(normalizedEmail, code, expiresAt);

        // Guardar en BD
        otpRepository.save(otpEntity);

        // Enviar email
        sendOtpEmail(normalizedEmail, code);

        return new OtpResponseDto(
                "Código OTP enviado al email. Válido por " + OTP_EXPIRATION_MINUTES + " minutos.",
                normalizedEmail
        );
    }

    @Override
    @Transactional
    public LoginResponseDto verifyOtp(String email, String code) {
        String normalizedEmail = normalizeEmail(email);

        // Buscar el OTP más reciente activo para este email
        OtpEntity otp = otpRepository.findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                normalizedEmail,
                LocalDateTime.now()
            )
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay un código OTP activo o válido para este email. Solicita uno nuevo."
                ));

        // Validar intentos fallidos
        if (otp.getAttemptCount() >= MAX_OTP_ATTEMPTS) {
            throw new IllegalStateException(
                    "Demasiados intentos fallidos. Solicita un nuevo código OTP."
            );
        }

        // Validar código
        if (!otp.getCode().equals(code)) {
            otp.setAttemptCount(otp.getAttemptCount() + 1);
            otpRepository.save(otp);
            throw new UnauthorizedException("Código OTP inválido.");
        }

        // Verificar que no haya expirado
        if (otp.isExpired()) {
            throw new IllegalArgumentException("El código OTP ha expirado. Solicita uno nuevo.");
        }

        // Marcar como verificado
        otp.setVerified(true);
        otpRepository.save(otp);

        // Buscar o crear usuario
        UserEntity user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseGet(() -> {
                    // Crear usuario temporal si no existe
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(normalizedEmail);
                    newUser.setPasswordHash("OTP_PENDING_PASSWORD");
                    newUser.setFirstName("Usuario");
                    newUser.setLastName("Temporal");
                    newUser.setDni(generateTemporaryDni());
                    newUser.setEnabled(true);
                    newUser.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(newUser);
                });

        // Generar JWT
        String token = jwtTokenProvider.generateToken(user.getId().toString());

        return new LoginResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getDni(),
                token
        );
    }

    @Override
    @Transactional
    public OtpResponseDto resendOtp(String email) {
        String normalizedEmail = normalizeEmail(email);

        // Buscar el OTP más reciente para este email (verificado o no)
        OtpEntity lastOtp = otpRepository.findTopByEmailOrderByCreatedAtDesc(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay un OTP pendiente para este email. Solicita uno nuevo primero."
                ));

        // Generar nuevo código
        String newCode = generateOtpCode();
        LocalDateTime newExpiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES);

        // Actualizar el OTP existente
        lastOtp.setCode(newCode);
        lastOtp.setExpiresAt(newExpiresAt);
        lastOtp.setAttemptCount(0); // Resetear intentos
        lastOtp.setVerified(false);

        otpRepository.save(lastOtp);

        // Enviar nuevo email
        sendOtpEmail(normalizedEmail, newCode);

        return new OtpResponseDto(
                "Nuevo código OTP reenviado. Válido por " + OTP_EXPIRATION_MINUTES + " minutos.",
                normalizedEmail
        );
    }

    private String generateOtpCode() {
        return String.format("%06d", RANDOM.nextInt(1000000));
    }

    private void sendOtpEmail(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(email);
            message.setSubject("Tu código de acceso - XploreNow");
            message.setText(
                    "Tu código OTP es: " + code + "\n\n"
                    + "Este código expirará en " + OTP_EXPIRATION_MINUTES + " minutos.\n\n"
                    + "Si no solicitaste este código, ignora este mensaje.\n\n"
                    + "XploreNow Team"
            );

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar email: " + e.getMessage(), e);
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private String generateTemporaryDni() {
        // users.dni es UNIQUE y length=20; esto evita colisiones con usuarios OTP temporales.
        return "OTP" + System.currentTimeMillis();
    }
}
