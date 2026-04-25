package com.example.desabackend.service;

import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpResponseDto;
import com.example.desabackend.entity.OtpEntity;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.UnauthorizedException;
import com.example.desabackend.repository.OtpRepository;
import com.example.desabackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock private OtpRepository otpRepository;
    @Mock private UserRepository userRepository;
    @Mock private JavaMailSender mailSender;
    @Mock private LoginResponseBuilder loginResponseBuilder;
    @Mock private PasswordEncoder passwordEncoder;

    private OtpServiceImpl otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpServiceImpl(
                otpRepository, userRepository, mailSender, loginResponseBuilder, passwordEncoder);
        ReflectionTestUtils.setField(otpService, "mailFrom", "noreply@test.com");
        ReflectionTestUtils.setField(otpService, "mailUsername", "noreply@test.com");
        ReflectionTestUtils.setField(otpService, "mailFailFast", false);
    }

    // ── requestSignupOtp ───────────────────────────────────────────────────────

    @Test
    void requestSignupOtp_pendingUser_createsOtpAndSendsEmail() {
        when(userRepository.findByEmailIgnoreCase("user@test.com"))
                .thenReturn(Optional.of(pendingUser(1L, "user@test.com")));

        OtpResponseDto result = otpService.requestSignupOtp("user@test.com");

        verify(otpRepository).save(any(OtpEntity.class));
        verify(mailSender).send(any(SimpleMailMessage.class));
        assertThat(result.email()).isEqualTo("user@test.com");
    }

    @Test
    void requestSignupOtp_activeUser_throwsIllegalArgument() {
        when(userRepository.findByEmailIgnoreCase("active@test.com"))
                .thenReturn(Optional.of(activeUser(1L, "active@test.com")));

        assertThatThrownBy(() -> otpService.requestSignupOtp("active@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya está registrado");
    }

    @Test
    void requestSignupOtp_noUser_createsOtpAnyway() {
        when(userRepository.findByEmailIgnoreCase("new@test.com")).thenReturn(Optional.empty());

        OtpResponseDto result = otpService.requestSignupOtp("new@test.com");

        verify(otpRepository).save(any(OtpEntity.class));
        assertThat(result.email()).isEqualTo("new@test.com");
    }

    // ── resendSignupOtp ────────────────────────────────────────────────────────

    @Test
    void resendSignupOtp_withExistingOtp_updatesAndResends() {
        when(userRepository.existsByEmailIgnoreCase("user@test.com")).thenReturn(true);
        OtpEntity existing = otpWithCode("user@test.com", "123456");
        when(otpRepository.findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc("user@test.com"))
                .thenReturn(Optional.of(existing));

        OtpResponseDto result = otpService.resendSignupOtp("user@test.com");

        verify(otpRepository).save(existing);
        verify(mailSender).send(any(SimpleMailMessage.class));
        assertThat(result.email()).isEqualTo("user@test.com");
    }

    @Test
    void resendSignupOtp_noOtpExists_throwsIllegalArgument() {
        when(userRepository.existsByEmailIgnoreCase("user@test.com")).thenReturn(true);
        when(otpRepository.findTopByEmailAndVerifiedFalseOrderByCreatedAtDesc("user@test.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> otpService.resendSignupOtp("user@test.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void resendSignupOtp_unknownEmail_throwsIllegalArgument() {
        when(userRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);

        assertThatThrownBy(() -> otpService.resendSignupOtp("nobody@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No existe");
    }

    // ── verifySignupOtpCode ────────────────────────────────────────────────────

    @Test
    void verifySignupOtpCode_correctCode_activatesPendingUserAndReturnsJwt() {
        UserEntity pending = pendingUser(1L, "user@test.com");
        OtpEntity otp = otpWithCode("user@test.com", "654321");
        LoginResponseDto expected = loginResponse(1L, "user@test.com", "jwt");

        when(userRepository.findByEmailIgnoreCase("user@test.com")).thenReturn(Optional.of(pending));
        when(otpRepository.findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                eq("user@test.com"), any(LocalDateTime.class))).thenReturn(Optional.of(otp));
        when(loginResponseBuilder.build(pending)).thenReturn(expected);

        LoginResponseDto result = otpService.verifySignupOtpCode("user@test.com", "654321");

        assertThat(result.token()).isEqualTo("jwt");
        assertThat(pending.getEnabled()).isTrue();
        verify(userRepository).save(pending);
    }

    @Test
    void verifySignupOtpCode_correctCodeAlreadyActive_returnsJwtWithoutResavingUser() {
        UserEntity active = activeUser(1L, "active@test.com");
        OtpEntity otp = otpWithCode("active@test.com", "111111");
        LoginResponseDto expected = loginResponse(1L, "active@test.com", "jwt");

        when(userRepository.findByEmailIgnoreCase("active@test.com")).thenReturn(Optional.of(active));
        when(otpRepository.findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                eq("active@test.com"), any(LocalDateTime.class))).thenReturn(Optional.of(otp));
        when(loginResponseBuilder.build(active)).thenReturn(expected);

        LoginResponseDto result = otpService.verifySignupOtpCode("active@test.com", "111111");

        assertThat(result.token()).isEqualTo("jwt");
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void verifySignupOtpCode_wrongCode_throwsUnauthorized() {
        when(userRepository.findByEmailIgnoreCase("user@test.com"))
                .thenReturn(Optional.of(activeUser(1L, "user@test.com")));
        when(otpRepository.findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                eq("user@test.com"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(otpWithCode("user@test.com", "999999")));

        assertThatThrownBy(() -> otpService.verifySignupOtpCode("user@test.com", "000000"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    void verifySignupOtpCode_tooManyAttempts_throwsIllegalState() {
        OtpEntity exhausted = otpWithCode("user@test.com", "999999");
        exhausted.setAttemptCount(5);

        when(userRepository.findByEmailIgnoreCase("user@test.com"))
                .thenReturn(Optional.of(activeUser(1L, "user@test.com")));
        when(otpRepository.findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                eq("user@test.com"), any(LocalDateTime.class))).thenReturn(Optional.of(exhausted));

        assertThatThrownBy(() -> otpService.verifySignupOtpCode("user@test.com", "111111"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("intentos");
    }

    @Test
    void verifySignupOtpCode_noActiveOtp_throwsIllegalArgument() {
        when(userRepository.findByEmailIgnoreCase("user@test.com"))
                .thenReturn(Optional.of(activeUser(1L, "user@test.com")));
        when(otpRepository.findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                eq("user@test.com"), any(LocalDateTime.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> otpService.verifySignupOtpCode("user@test.com", "123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("activo");
    }

    // ── sendLoginOtp ───────────────────────────────────────────────────────────

    @Test
    void sendLoginOtp_activeUser_createsOtpAndSendsEmail() {
        when(userRepository.findByEmailIgnoreCase("user@test.com"))
                .thenReturn(Optional.of(activeUser(1L, "user@test.com")));

        OtpResponseDto result = otpService.sendLoginOtp("user@test.com");

        verify(otpRepository).save(any(OtpEntity.class));
        verify(mailSender).send(any(SimpleMailMessage.class));
        assertThat(result.email()).isEqualTo("user@test.com");
    }

    @Test
    void sendLoginOtp_unknownUser_throwsIllegalArgument() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> otpService.sendLoginOtp("nobody@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No existe");
    }

    @Test
    void sendLoginOtp_pendingUser_throwsIllegalArgument() {
        when(userRepository.findByEmailIgnoreCase("pending@test.com"))
                .thenReturn(Optional.of(pendingUser(1L, "pending@test.com")));

        assertThatThrownBy(() -> otpService.sendLoginOtp("pending@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("verificada");
    }

    // ── verifyLoginOtp ─────────────────────────────────────────────────────────

    @Test
    void verifyLoginOtp_activeUser_returnsJwtWithoutChangingEnabledState() {
        UserEntity active = activeUser(1L, "active@test.com");
        OtpEntity otp = otpWithCode("active@test.com", "123456");
        LoginResponseDto expected = loginResponse(1L, "active@test.com", "jwt");

        when(userRepository.findByEmailIgnoreCase("active@test.com")).thenReturn(Optional.of(active));
        when(otpRepository.findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                eq("active@test.com"), any(LocalDateTime.class))).thenReturn(Optional.of(otp));
        when(loginResponseBuilder.build(active)).thenReturn(expected);

        LoginResponseDto result = otpService.verifyLoginOtp("active@test.com", "123456");

        assertThat(result.token()).isEqualTo("jwt");
        // Usuario activo no debe ser guardado de nuevo (no cambió enabled)
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void verifyLoginOtp_pendingUser_throwsIllegalArgument() {
        when(userRepository.findByEmailIgnoreCase("pending@test.com"))
                .thenReturn(Optional.of(pendingUser(1L, "pending@test.com")));

        assertThatThrownBy(() -> otpService.verifyLoginOtp("pending@test.com", "123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("verificada");
    }

    @Test
    void verifyLoginOtp_unknownUser_throwsIllegalArgument() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> otpService.verifyLoginOtp("nobody@test.com", "123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No existe");
    }

    @Test
    void verifyLoginOtp_wrongCode_throwsUnauthorized() {
        when(userRepository.findByEmailIgnoreCase("active@test.com"))
                .thenReturn(Optional.of(activeUser(1L, "active@test.com")));
        when(otpRepository.findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                eq("active@test.com"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(otpWithCode("active@test.com", "999999")));

        assertThatThrownBy(() -> otpService.verifyLoginOtp("active@test.com", "000000"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("inválido");
    }

    @Test
    void verifyLoginOtp_noActiveOtp_throwsIllegalArgument() {
        when(userRepository.findByEmailIgnoreCase("active@test.com"))
                .thenReturn(Optional.of(activeUser(1L, "active@test.com")));
        when(otpRepository.findTopByEmailAndVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                eq("active@test.com"), any(LocalDateTime.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> otpService.verifyLoginOtp("active@test.com", "123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("activo");
    }

    // ── email failure (mailFailFast=false) ─────────────────────────────────────

    @Test
    void requestSignupOtp_emailFails_returnsWarningMessageAndOtpIsStillSaved() {
        // Arrange: la cuenta está pendiente de verificación
        when(userRepository.findByEmailIgnoreCase("user@test.com"))
                .thenReturn(Optional.of(pendingUser(1L, "user@test.com")));
        // mail.fail-fast=false → MailException no se propaga
        doThrow(new org.springframework.mail.MailSendException("SMTP unreachable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        OtpResponseDto result = otpService.requestSignupOtp("user@test.com");

        // El OTP debe haberse guardado aunque el email no se enviara
        verify(otpRepository).save(any(OtpEntity.class));
        // El resultado avisa que el email no se pudo enviar
        assertThat(result.email()).isEqualTo("user@test.com");
        assertThat(result.message()).containsIgnoringCase("no se pudo");
    }

    @Test
    void sendLoginOtp_emailFails_returnsWarningMessageAndOtpIsStillSaved() {
        when(userRepository.findByEmailIgnoreCase("user@test.com"))
                .thenReturn(Optional.of(activeUser(1L, "user@test.com")));
        doThrow(new org.springframework.mail.MailSendException("SMTP unreachable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        OtpResponseDto result = otpService.sendLoginOtp("user@test.com");

        verify(otpRepository).save(any(OtpEntity.class));
        assertThat(result.email()).isEqualTo("user@test.com");
        assertThat(result.message()).containsIgnoringCase("no se pudo");
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private static UserEntity activeUser(Long id, String email) {
        UserEntity u = new UserEntity();
        u.setId(id);
        u.setEmail(email);
        u.setPasswordHash("hash");
        u.setFirstName("Test");
        u.setLastName("User");
        u.setEnabled(true);
        return u;
    }

    private static UserEntity pendingUser(Long id, String email) {
        UserEntity u = activeUser(id, email);
        u.setEnabled(false);
        return u;
    }

    private static OtpEntity otpWithCode(String email, String code) {
        return new OtpEntity(email, code, LocalDateTime.now().plusMinutes(10));
    }

    private static LoginResponseDto loginResponse(Long userId, String email, String token) {
        return new LoginResponseDto(userId, email, "Test", "User", token, "refresh");
    }
}