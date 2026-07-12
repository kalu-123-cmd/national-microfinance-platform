package com.microfinance.auth.service;

import com.microfinance.auth.domain.model.*;
import com.microfinance.auth.domain.model.OtpPurpose;
import com.microfinance.auth.domain.repository.*;
import com.microfinance.auth.dto.*;
import com.microfinance.common.exception.*;
import com.microfinance.event.KafkaTopics;
import com.microfinance.event.notification.NotificationEmailEvent;
import com.microfinance.event.notification.NotificationSmsEvent;
import com.microfinance.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserCredentialRepository credentialRepository;
    private final OtpRecordRepository otpRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final KafkaOperations<String, Object> kafkaTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${auth.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${auth.lockout-duration-minutes:30}")
    private int lockoutDurationMinutes;

    @Value("${auth.refresh-token-validity-days:30}")
    private int refreshTokenValidityDays;

    @Value("${auth.otp-validity-minutes:5}")
    private int otpValidityMinutes;

    @Value("${auth.otp-max-attempts:3}")
    private int otpMaxAttempts;

    @Transactional
    public void registerCredential(RegisterCredentialRequest request) {
        log.info("Registering credential for userId: {}", request.getUserId());

        if (credentialRepository.existsByUserId(request.getUserId())) {
            throw new BusinessException("Credential already exists for user");
        }
        if (credentialRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException("Phone number already registered");
        }
        if (request.getEmail() != null && credentialRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already registered");
        }

        UserCredential credential = UserCredential.builder()
            .userId(request.getUserId())
            .phoneNumber(request.getPhoneNumber())
            .email(request.getEmail())
            .passwordHash(request.getPassword() != null
                ? passwordEncoder.encode(request.getPassword())
                : null)
            .pinHash(passwordEncoder.encode(request.getPin()))
            .enabled(true)
            .build();

        credentialRepository.save(credential);
        log.info("Credential registered successfully for userId: {}", request.getUserId());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for identifier: {}, method: {}", request.getIdentifier(), request.getLoginMethod());

        UserCredential credential = findByIdentifier(request.getIdentifier());

        if (!credential.isEnabled()) {
            throw new UnauthorizedException("Account is disabled");
        }
        if (credential.isAccountLocked()) {
            throw new UnauthorizedException("Account is locked. Try again later.");
        }

        boolean authenticated = switch (request.getLoginMethod()) {
            case PASSWORD -> authenticatePassword(credential, request.getPassword());
            case PIN      -> authenticatePin(credential, request.getPin());
            case BIOMETRIC -> throw new BusinessException("Biometric auth not yet implemented");
            default        -> throw new BusinessException("OTP login uses /otp/verify endpoint");
        };

        if (!authenticated) {
            handleFailedLogin(credential);
            throw new UnauthorizedException("Invalid credentials");
        }

        // Reset failed attempts on success
        credential.setFailedLoginAttempts(0);
        credential.setAccountLockedUntil(null);
        credential.setLastLoginAt(Instant.now());
        credential.setLastLoginIp(request.getIpAddress());
        credential.setLastDeviceId(request.getDeviceId());
        credentialRepository.save(credential);

        return generateTokenResponse(credential, request.getDeviceId(), request.getDeviceInfo(), request.getIpAddress());
    }

    @Transactional
    public void sendOtp(OtpSendRequest request) {
        log.info("Sending OTP to: {}, purpose: {}", request.getRecipient(), request.getPurpose());

        // Delete previous OTPs for same recipient + purpose
        otpRepository.deleteByRecipientAndPurpose(request.getRecipient(), request.getPurpose());

        String otpCode  = generateOtpCode();
        String otpHash  = hashSha256(otpCode);

        OtpRecord otpRecord = OtpRecord.builder()
            .recipient(request.getRecipient())
            .otpHash(otpHash)
            .purpose(request.getPurpose())
            .expiresAt(Instant.now().plus(Duration.ofMinutes(otpValidityMinutes)))
            .build();

        otpRepository.save(otpRecord);
        publishOtpNotification(request.getRecipient(), otpCode);
        log.info("OTP generated for {}, hash prefix: {}", request.getRecipient(), otpHash.substring(0, 8));
    }

    private void publishOtpNotification(String recipient, String otpCode) {
        if (recipient.contains("@")) {
            NotificationEmailEvent emailEvent = new NotificationEmailEvent(
                null,
                recipient,
                "Your OTP Code",
                "Your OTP code is " + otpCode + ". Valid for " + otpValidityMinutes + " minutes.",
                null
            );
            kafkaTemplate.send(KafkaTopics.NOTIFY_EMAIL, recipient, emailEvent);
            log.debug("Published OTP email event for recipient={}", recipient);
        } else {
            NotificationSmsEvent smsEvent = new NotificationSmsEvent(
                null,
                recipient,
                "Your OTP code is " + otpCode + ". Valid for " + otpValidityMinutes + " minutes.",
                null
            );
            kafkaTemplate.send(KafkaTopics.NOTIFY_SMS, recipient, smsEvent);
            log.debug("Published OTP SMS event for recipient={}", recipient);
        }
    }

    @Transactional
    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        log.info("Verifying OTP for recipient: {}, purpose: {}", request.getRecipient(), request.getPurpose());

        OtpRecord otpRecord = otpRepository
            .findLatestValidOtp(request.getRecipient(), request.getPurpose(), Instant.now())
            .orElseThrow(() -> new UnauthorizedException("Invalid or expired OTP"));

        if (otpRecord.getVerificationAttempts() >= otpMaxAttempts) {
            throw new UnauthorizedException("Maximum OTP attempts exceeded");
        }

        String providedHash = hashSha256(request.getOtpCode());
        if (!providedHash.equals(otpRecord.getOtpHash())) {
            otpRecord.setVerificationAttempts(otpRecord.getVerificationAttempts() + 1);
            otpRepository.save(otpRecord);
            throw new UnauthorizedException("Invalid OTP code");
        }

        otpRecord.setVerified(true);
        otpRecord.setVerifiedAt(Instant.now());
        otpRepository.save(otpRecord);

        if (request.getPurpose() == OtpPurpose.LOGIN) {
            UserCredential credential = findByIdentifier(request.getRecipient());
            return generateTokenResponse(credential, request.getDeviceId(), request.getDeviceInfo(), null);
        }

        log.info("OTP verified successfully for recipient: {}", request.getRecipient());
        return null;
    }

    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        log.info("Refreshing access token");

        String tokenHash = hashSha256(request.getRefreshToken());
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
            .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (!refreshToken.isValid()) {
            throw new UnauthorizedException("Refresh token is revoked or expired");
        }

        // Rotate: revoke old, issue new
        refreshTokenRepository.revokeToken(refreshToken.getId(), Instant.now(), "TOKEN_ROTATED");

        UserCredential credential = credentialRepository.findByUserId(refreshToken.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));

        return generateTokenResponse(credential, refreshToken.getDeviceId(),
                                     refreshToken.getDeviceInfo(), refreshToken.getIpAddress());
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        UserCredential credential = credentialRepository.findByUserId(request.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (credential.getPasswordHash() == null ||
            !passwordEncoder.matches(request.getCurrentPassword(), credential.getPasswordHash())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        credential.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        credentialRepository.save(credential);
        log.info("Password changed for userId: {}", request.getUserId());
    }

    @Transactional
    public void changePin(ChangePinRequest request) {
        UserCredential credential = credentialRepository.findByUserId(request.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPin(), credential.getPinHash())) {
            throw new UnauthorizedException("Current PIN is incorrect");
        }

        credential.setPinHash(passwordEncoder.encode(request.getNewPin()));
        credential.setPinChangedAt(Instant.now());
        credentialRepository.save(credential);
        log.info("PIN changed for userId: {}", request.getUserId());
    }

    @Transactional
    public void logout(String userId, String refreshToken) {
        if (refreshToken != null) {
            String tokenHash = hashSha256(refreshToken);
            refreshTokenRepository.findByTokenHash(tokenHash)
                .ifPresent(t -> refreshTokenRepository.revokeToken(t.getId(), Instant.now(), "USER_LOGOUT"));
        }
        log.info("User logged out: {}", userId);
    }

    @Transactional
    public void revokeAllSessions(String userId) {
        refreshTokenRepository.revokeAllUserTokens(userId, Instant.now(), "ALL_SESSIONS_REVOKED");
        log.info("All sessions revoked for userId: {}", userId);
    }

    // ─── Private helpers ───────────────────────────────────────────────────────

    private UserCredential findByIdentifier(String identifier) {
        if (identifier.startsWith("+")) {
            return credentialRepository.findByPhoneNumber(identifier)
                .orElseThrow(() -> new NotFoundException("User not found"));
        } else if (identifier.contains("@")) {
            return credentialRepository.findByEmail(identifier)
                .orElseThrow(() -> new NotFoundException("User not found"));
        } else {
            return credentialRepository.findByUserId(identifier)
                .orElseThrow(() -> new NotFoundException("User not found"));
        }
    }

    private boolean authenticatePassword(UserCredential credential, String password) {
        if (password == null || credential.getPasswordHash() == null) return false;
        return passwordEncoder.matches(password, credential.getPasswordHash());
    }

    private boolean authenticatePin(UserCredential credential, String pin) {
        if (pin == null) return false;
        return passwordEncoder.matches(pin, credential.getPinHash());
    }

    private void handleFailedLogin(UserCredential credential) {
        int attempts = credential.getFailedLoginAttempts() + 1;
        credential.setFailedLoginAttempts(attempts);
        credential.setLastFailedLoginAt(Instant.now());

        if (attempts >= maxFailedAttempts) {
            Instant lockUntil = Instant.now().plus(Duration.ofMinutes(lockoutDurationMinutes));
            credential.setAccountLockedUntil(lockUntil);
            log.warn("Account locked for userId: {} until {}", credential.getUserId(), lockUntil);
        }
        credentialRepository.save(credential);
    }

    private AuthResponse generateTokenResponse(UserCredential credential,
                                               String deviceId, String deviceInfo, String ipAddress) {
        // generateAccessToken(userId, phone, roles, extraClaims)
        String accessToken = jwtTokenProvider.generateAccessToken(
            credential.getUserId(),
            credential.getPhoneNumber(),
            List.of("USER"),
            null
        );

        String rawRefreshToken = generateRefreshToken();
        String tokenHash       = hashSha256(rawRefreshToken);

        RefreshToken refreshToken = RefreshToken.builder()
            .userId(credential.getUserId())
            .tokenHash(tokenHash)
            .deviceId(deviceId)
            .deviceInfo(deviceInfo)
            .ipAddress(ipAddress)
            .expiresAt(Instant.now().plus(Duration.ofDays(refreshTokenValidityDays)))
            .build();

        refreshTokenRepository.save(refreshToken);

        long expiresInSeconds = jwtTokenProvider.getAccessTokenExpiryMs() / 1000;

        return AuthResponse.builder()
            .userId(credential.getUserId())
            .accessToken(accessToken)
            .refreshToken(rawRefreshToken)
            .tokenType("Bearer")
            .expiresIn(expiresInSeconds)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(expiresInSeconds))
            .scopes(new String[]{"USER"})
            .sessionId(refreshToken.getId())
            .build();
    }

    private String generateOtpCode() {
        return String.format("%06d", secureRandom.nextInt(1_000_000));
    }

    private String generateRefreshToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
