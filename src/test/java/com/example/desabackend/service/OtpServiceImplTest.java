package com.example.desabackend.service;

import com.example.desabackend.entity.OtpEntity;
import com.example.desabackend.repository.OtpRepository;
import com.example.desabackend.repository.UserRepository;
import com.example.desabackend.services.interfaces.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock private OtpRepository otpRepository;
    @Mock private UserRepository userRepository;
    @Mock private JavaMailSender mailSender;
    @Mock private LoginResponseBuilder loginResponseBuilder;
    @Mock private IAuthService authService;
    @Mock private PasswordEncoder passwordEncoder;

    private OtpServiceImpl service;

    private static final String EMAIL = "user@example.com";

    @BeforeEach
    void setUp() {
        service = new OtpServiceImpl(
                otpRepository, userRepository, mailSender,
                loginResponseBuilder, authService, passwordEncoder);
        ReflectionTestUtils.setField(service, "mailFrom", "noreply@xplorenow.com");
    }

    // ── requestSignupOtp ──────────────────────────────────────────────────────

    @Test
    void requestSignupOtp_whenEmailSendFails_throwsRuntimeException() {
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);
        when(otpRepository.save(any(OtpEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new MailSendException("SMTP unreachable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() -> service.requestSignupOtp(EMAIL))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se pudo enviar el email OTP")
                .hasCauseInstanceOf(MailSendException.class);
    }

    @Test
    void requestSignupOtp_whenEmailSendFails_otpIsSavedBeforeThrow() {
        // The OTP is persisted before send is attempted; the @Transactional rollback
        // is handled by Spring — here we verify save() was called exactly once.
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);
        when(otpRepository.save(any(OtpEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new MailSendException("SMTP unreachable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        try { service.requestSignupOtp(EMAIL); } catch (RuntimeException ignored) {}

        verify(otpRepository, times(1)).save(any(OtpEntity.class));
    }

    @Test
    void requestSignupOtp_whenEmailSendSucceeds_returnsResponse() {
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);
        when(otpRepository.save(any(OtpEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        var result = service.requestSignupOtp(EMAIL);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(EMAIL);
        assertThat(result.message()).contains("OTP");
    }

    @Test
    void requestSignupOtp_whenEmailAlreadyRegistered_throwsIllegalArgument() {
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> service.requestSignupOtp(EMAIL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya está registrado");

        verifyNoInteractions(mailSender, otpRepository);
    }

    @Test
    void requestSignupOtp_sendsEmailToCorrectAddress() {
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);
        when(otpRepository.save(any(OtpEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        service.requestSignupOtp(EMAIL);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getTo()).containsExactly(EMAIL);
        assertThat(sent.getFrom()).isEqualTo("noreply@xplorenow.com");
        assertThat(sent.getSubject()).isNotBlank();
    }

    // ── requestPasswordReset ──────────────────────────────────────────────────

    @Test
    void requestPasswordReset_whenEmailSendFails_throwsRuntimeException() {
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(true);
        when(otpRepository.save(any(OtpEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new MailSendException("SMTP unreachable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThatThrownBy(() -> service.requestPasswordReset(EMAIL))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se pudo enviar el email OTP");
    }

    @Test
    void requestPasswordReset_whenEmailNotRegistered_returnsGenericResponseWithoutSendingEmail() {
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(false);

        var result = service.requestPasswordReset(EMAIL);

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(EMAIL);
        verifyNoInteractions(mailSender, otpRepository);
    }
}
