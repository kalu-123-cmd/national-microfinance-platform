package com.microfinance.auth.domain.service;

import com.microfinance.auth.domain.model.OtpPurpose;
import com.microfinance.auth.domain.model.OtpRecord;
import com.microfinance.auth.domain.repository.OtpRecordRepository;
import com.microfinance.common.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private OtpRecordRepository repository;

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService(repository);
        ReflectionTestUtils.setField(otpService, "otpValidityMinutes", 5);
        ReflectionTestUtils.setField(otpService, "maxAttempts", 3);
    }

    @Test
    void generateOtpDeletesPreviousOtpAndStoresHashedSixDigitCode() {
        String code = otpService.generateOtp("+251911111111", OtpPurpose.LOGIN);

        ArgumentCaptor<OtpRecord> recordCaptor = ArgumentCaptor.forClass(OtpRecord.class);
        verify(repository).deleteByRecipientAndPurpose("+251911111111", OtpPurpose.LOGIN);
        verify(repository).save(recordCaptor.capture());

        OtpRecord savedRecord = recordCaptor.getValue();
        assertThat(code).matches("\\d{6}");
        assertThat(savedRecord.getRecipient()).isEqualTo("+251911111111");
        assertThat(savedRecord.getPurpose()).isEqualTo(OtpPurpose.LOGIN);
        assertThat(savedRecord.getOtpHash()).isNotEqualTo(code);
        assertThat(savedRecord.getExpiresAt()).isNotNull();
    }

    @Test
    void verifyOtpMarksRecordVerifiedWhenCodeMatches() {
        String code = otpService.generateOtp("+251922222222", OtpPurpose.REGISTER);
        ArgumentCaptor<OtpRecord> recordCaptor = ArgumentCaptor.forClass(OtpRecord.class);
        verify(repository).save(recordCaptor.capture());
        OtpRecord savedRecord = recordCaptor.getValue();

        when(repository.findLatestValidOtp(eq("+251922222222"), eq(OtpPurpose.REGISTER), any()))
            .thenReturn(Optional.of(savedRecord));

        otpService.verifyOtp("+251922222222", code, OtpPurpose.REGISTER);

        assertThat(savedRecord.isVerified()).isTrue();
        assertThat(savedRecord.getVerifiedAt()).isNotNull();
        verify(repository).save(savedRecord);
    }

    @Test
    void verifyOtpIncrementsAttemptsAndRejectsInvalidCode() {
        OtpRecord record = OtpRecord.builder()
            .recipient("+251933333333")
            .purpose(OtpPurpose.PASSWORD_RESET)
            .otpHash("not-the-provided-code-hash")
            .verificationAttempts(1)
            .build();

        when(repository.findLatestValidOtp(eq("+251933333333"), eq(OtpPurpose.PASSWORD_RESET), any()))
            .thenReturn(Optional.of(record));

        assertThatThrownBy(() -> otpService.verifyOtp("+251933333333", "123456", OtpPurpose.PASSWORD_RESET))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessage("Invalid OTP code");

        assertThat(record.getVerificationAttempts()).isEqualTo(2);
        verify(repository).save(record);
    }
}
