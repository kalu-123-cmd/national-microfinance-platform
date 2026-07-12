package com.microfinance.auth.domain.repository;

import com.microfinance.auth.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUserId(String userId);

    @Query("SELECT r FROM RefreshToken r WHERE r.userId = :userId AND r.revoked = false AND r.expiresAt > :now")
    List<RefreshToken> findActiveTokensByUserId(@Param("userId") String userId, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true, r.revokedAt = :timestamp, r.revocationReason = :reason WHERE r.id = :id")
    void revokeToken(@Param("id") String id, @Param("timestamp") Instant timestamp, @Param("reason") String reason);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true, r.revokedAt = :timestamp, r.revocationReason = :reason WHERE r.userId = :userId AND r.revoked = false")
    void revokeAllUserTokens(@Param("userId") String userId, @Param("timestamp") Instant timestamp, @Param("reason") String reason);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < :timestamp")
    void deleteExpiredTokens(@Param("timestamp") Instant timestamp);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.userId = :userId AND r.deviceId = :deviceId")
    void deleteByUserIdAndDeviceId(@Param("userId") String userId, @Param("deviceId") String deviceId);

    long countByUserIdAndRevokedFalse(String userId);
}
