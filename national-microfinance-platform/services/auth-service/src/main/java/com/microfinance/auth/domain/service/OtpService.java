package com.microfinance.auth.domain.service;

import com.microfinance.auth.domain.model.OtpPurpose;
import com.microfinance.auth.domain.model.OtpRecord;
import com.microfinance.auth.domain.repository.OtpRecordRepository;
import com.microfinance.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpRecordRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${auth.otp-validity-minutes:5}")
    private int otpValidityMinutes;

    @Value("${auth.otp-max-attempts:3}")
    private int maxAttempts;

    /**
     * Generate a new OTP for the given recipient and purpose.
     * Any previous OTP for the same recipient+purpose is deleted first.
     * Returns the plain-text code (to be sent via SMS/email).
     */
    @Transactional
    public String generateOtp(String recipient, OtpPurpose purpose) {
        // Delete any previous OTP for this recipient+purpose
        repository.deleteByRecipientAndPurpose(recipient, purpose);

        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        String hash = sha256(code);

        OtpRecord record = OtpRecord.builder()
            .recipient(recipient)
            .otpHash(hash)
            .purpose(purpose)
            .expiresAt(Instant.now().plus(otpValidityMinutes, ChronoUnit.MINUTES))
            .build();

        repository.save(record);
        log.info("OTP generated for recipient: {}, purpose: {}", recipient, purpose);
        return code; // caller passes this to notification-service
    }

    /**
     * Verify an OTP code. Throws UnauthorizedException if invalid/expired/exceeded.
     */
    @Transactional
    public void verifyOtp(String recipient, String otpCode, OtpPurpose purpose) {
        OtpRecord record = repository
            .findLatestValidOtp(recipient, purpose, Instant.now())
            .orElseThrow(() -> new UnauthorizedException("OTP not found or expired"));

        if (record.getVerificationAttempts() >= maxAttempts) {
            throw new UnauthorizedException("Maximum OTP attempts exceeded. Request a new OTP.");
        }

        String providedHash = sha256(otpCode);
        if (!providedHash.equals(record.getOtpHash())) {
            record.setVerificationAttempts(record.getVerificationAttempts() + 1);
            repository.save(record);
            throw new UnauthorizedException("Invalid OTP code");
        }

        record.setVerified(true);
        record.setVerifiedAt(Instant.now());
        repository.save(record);
        log.info("OTP verified successfully for recipient: {}, purpose: {}", recipient, purpose);
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
