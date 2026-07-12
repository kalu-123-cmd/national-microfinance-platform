package com.microfinance.auth.domain.repository;

import com.microfinance.auth.domain.model.OtpRecord;
import com.microfinance.auth.domain.model.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.Optional;

@Repository
public interface OtpRecordRepository extends JpaRepository<OtpRecord, String> {

    Optional<OtpRecord> findTopByRecipientAndPurposeOrderByCreatedAtDesc(String recipient, OtpPurpose purpose);

    @Query("SELECT o FROM OtpRecord o WHERE o.recipient = :recipient AND o.purpose = :purpose AND o.expiresAt > :now AND o.verified = false ORDER BY o.createdAt DESC")
    Optional<OtpRecord> findLatestValidOtp(@Param("recipient") String recipient, 
                                           @Param("purpose") OtpPurpose purpose, 
                                           @Param("now") Instant now);

    @Modifying
    @Query("UPDATE OtpRecord o SET o.verificationAttempts = :attempts WHERE o.id = :id")
    void updateVerificationAttempts(@Param("id") String id, @Param("attempts") int attempts);

    @Modifying
    @Query("UPDATE OtpRecord o SET o.verified = true, o.verifiedAt = :timestamp WHERE o.id = :id")
    void markAsVerified(@Param("id") String id, @Param("timestamp") Instant timestamp);

    @Modifying
    @Query("DELETE FROM OtpRecord o WHERE o.expiresAt < :timestamp")
    void deleteExpiredOtps(@Param("timestamp") Instant timestamp);

    @Modifying
    @Query("DELETE FROM OtpRecord o WHERE o.recipient = :recipient AND o.purpose = :purpose")
    void deleteByRecipientAndPurpose(@Param("recipient") String recipient, @Param("purpose") OtpPurpose purpose);
}
