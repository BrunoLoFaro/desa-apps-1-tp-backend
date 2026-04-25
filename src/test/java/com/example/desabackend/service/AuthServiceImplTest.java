package com.example.desabackend.service;

import com.example.desabackend.dto.LoginRequestDto;
import com.example.desabackend.dto.LoginResponseDto;
import com.example.desabackend.dto.OtpResponseDto;
import com.example.desabackend.dto.RefreshRequestDto;
import com.example.desabackend.dto.RefreshResponseDto;
import com.example.desabackend.dto.RegisterRequestDto;
import com.example.desabackend.entity.UserEntity;
import com.example.desabackend.exception.UnauthorizedException;
import com.example.desabackend.repository.UserRepository;
import com.example.desabackend.services.interfaces.IOtpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private LoginResponseBuilder loginResponseBuilder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private IOtpService otpService;

    @InjectMocks private AuthServiceImpl authService;

    // ── login ──────────────────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returnsToken() {
        UserEntity user = activeUser(1L, "user@test.com", "hash");
        LoginResponseDto expected = loginResponse(1L, "user@test.com", "jwt123");

        when(userRepository.findByEmailIgnoreCase("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hash")).thenReturn(true);
        when(loginResponseBuilder.build(user)).thenReturn(expected);

        LoginResponseDto result = authService.login(new LoginRequestDto("user@test.com", "secret"));

        assertThat(result.token()).isEqualTo("jwt123");
        assertThat(result.userId()).isEqualTo(1L);
    }

    @Test
    void login_emailNormalized_lookupIsLowercase() {
        UserEntity user = activeUser(1L, "user@test.com", "hash");
        LoginResponseDto expected = loginResponse(1L, "user@test.com", "jwt");

        when(userRepository.findByEmailIgnoreCase("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "hash")).thenReturn(true);
        when(loginResponseBuilder.build(user)).thenReturn(expected);

        authService.login(new LoginRequestDto("USER@TEST.COM", "secret"));

        verify(userRepository).findByEmailIgnoreCase("user@test.com");
    }

    @Test
    void login_unknownEmail_throwsUnauthorized() {
        when(userRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequestDto("nobody@test.com", "pass")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Credenciales");
    }

    @Test
    void login_unverifiedAccount_throwsUnauthorized() {
        UserEntity pending = pendingUser(1L, "pending@test.com", "hash");
        when(userRepository.findByEmailIgnoreCase("pending@test.com")).thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> authService.login(new LoginRequestDto("pending@test.com", "pass")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("verificada");
    }

    @Test
    void login_wrongPassword_throwsUnauthorized() {
        UserEntity user = activeUser(1L, "user@test.com", "hash");
        when(userRepository.findByEmailIgnoreCase("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequestDto("user@test.com", "wrong")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Credenciales");
    }

    // ── register ───────────────────────────────────────────────────────────────

    @Test
    void register_newUser_savesWithEnabledFalseAndSendsOtp() {
        when(userRepository.findByEmailIgnoreCase("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password1@")).thenReturn("hashed");
        when(otpService.requestSignupOtp("new@test.com"))
                .thenReturn(new OtpResponseDto("OTP enviado.", "new@test.com"));

        OtpResponseDto result = authService.register(
                new RegisterRequestDto("new@test.com", "Password1@", "Juan", "Perez", null));

        verify(userRepository).save(argThat(u ->
                "new@test.com".equals(u.getEmail())
                && Boolean.FALSE.equals(u.getEnabled())
                && "hashed".equals(u.getPasswordHash())));
        assertThat(result.email()).isEqualTo("new@test.com");
    }

    @Test
    void register_existingActiveUser_throwsIllegalArgument() {
        UserEntity active = activeUser(1L, "active@test.com", "hash");
        when(userRepository.findByEmailIgnoreCase("active@test.com")).thenReturn(Optional.of(active));

        assertThatThrownBy(() -> authService.register(
                new RegisterRequestDto("active@test.com", "Password1@", "Juan", "Perez", null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya está registrado");
    }

    @Test
    void register_existingPendingUser_updatesDataAndResends() {
        UserEntity pending = pendingUser(1L, "pending@test.com", "oldHash");
        when(userRepository.findByEmailIgnoreCase("pending@test.com")).thenReturn(Optional.of(pending));
        when(passwordEncoder.encode("NewPass1@")).thenReturn("newHash");
        when(otpService.requestSignupOtp("pending@test.com"))
                .thenReturn(new OtpResponseDto("OTP reenviado.", "pending@test.com"));

        OtpResponseDto result = authService.register(
                new RegisterRequestDto("pending@test.com", "NewPass1@", "Maria", "Lopez", "1122334455"));

        verify(userRepository).save(pending);
        assertThat(pending.getPasswordHash()).isEqualTo("newHash");
        assertThat(pending.getFirstName()).isEqualTo("Maria");
        assertThat(result.email()).isEqualTo("pending@test.com");
    }

    @Test
    void register_emailNormalized_savedAsLowercase() {
        when(userRepository.findByEmailIgnoreCase("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(otpService.requestSignupOtp("new@test.com"))
                .thenReturn(new OtpResponseDto("OTP enviado.", "new@test.com"));

        authService.register(new RegisterRequestDto("NEW@TEST.COM", "Password1@", "Juan", "Perez", null));

        verify(userRepository).save(argThat(u -> "new@test.com".equals(u.getEmail())));
    }

    @Test
    void register_emailDeliveryFails_userIsStillSaved() {
        // mailFailFast=false: OtpService absorbs the MailException and returns a warning response.
        // The transaction must NOT roll back — the user entity should be persisted regardless.
        when(userRepository.findByEmailIgnoreCase("new@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password1@")).thenReturn("hashed");
        when(otpService.requestSignupOtp("new@test.com"))
                .thenReturn(new OtpResponseDto(
                        "Generamos tu código OTP, pero no se pudo enviar el email.", "new@test.com"));

        OtpResponseDto result = authService.register(
                new RegisterRequestDto("new@test.com", "Password1@", "Juan", "Perez", null));

        verify(userRepository).save(argThat(u ->
                "new@test.com".equals(u.getEmail()) && Boolean.FALSE.equals(u.getEnabled())));
        assertThat(result.message()).containsIgnoringCase("no se pudo");
    }

    // ── refresh ────────────────────────────────────────────────────────────────

    @Test
    void refresh_validToken_returnsNewTokens() {
        UserEntity user = activeUser(1L, "user@test.com", "hash");
        when(jwtTokenProvider.validateRefreshToken("oldRefresh")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("oldRefresh")).thenReturn("1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken("1")).thenReturn("newAccess");
        when(jwtTokenProvider.generateRefreshToken("1")).thenReturn("newRefresh");

        RefreshResponseDto result = authService.refresh(new RefreshRequestDto("oldRefresh"));

        assertThat(result.token()).isEqualTo("newAccess");
        assertThat(result.refreshToken()).isEqualTo("newRefresh");
    }

    @Test
    void refresh_invalidToken_throwsUnauthorized() {
        when(jwtTokenProvider.validateRefreshToken("bad")).thenReturn(false);

        assertThatThrownBy(() -> authService.refresh(new RefreshRequestDto("bad")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void refresh_userNotFound_throwsUnauthorized() {
        when(jwtTokenProvider.validateRefreshToken("tok")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("tok")).thenReturn("99");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh(new RefreshRequestDto("tok")))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void refresh_disabledUser_throwsUnauthorized() {
        when(jwtTokenProvider.validateRefreshToken("tok")).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken("tok")).thenReturn("1");
        when(userRepository.findById(1L)).thenReturn(Optional.of(pendingUser(1L, "u@test.com", "h")));

        assertThatThrownBy(() -> authService.refresh(new RefreshRequestDto("tok")))
                .isInstanceOf(UnauthorizedException.class);
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private static UserEntity activeUser(Long id, String email, String hash) {
        UserEntity u = new UserEntity();
        u.setId(id);
        u.setEmail(email);
        u.setPasswordHash(hash);
        u.setFirstName("Test");
        u.setLastName("User");
        u.setEnabled(true);
        return u;
    }

    private static UserEntity pendingUser(Long id, String email, String hash) {
        UserEntity u = activeUser(id, email, hash);
        u.setEnabled(false);
        return u;
    }

    private static LoginResponseDto loginResponse(Long userId, String email, String token) {
        return new LoginResponseDto(userId, email, "Test", "User", token, "refresh");
    }
}
