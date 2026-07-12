package com.microfinance.auth.domain.repository;

import com.microfinance.auth.domain.model.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, String> {

    Optional<UserCredential> findByUserId(String userId);
    
    Optional<UserCredential> findByPhoneNumber(String phoneNumber);
    
    Optional<UserCredential> findByEmail(String email);
    
    boolean existsByUserId(String userId);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE UserCredential u SET u.failedLoginAttempts = :attempts, u.lastFailedLoginAt = :timestamp WHERE u.userId = :userId")
    void updateFailedLoginAttempts(@Param("userId") String userId, @Param("attempts") int attempts, @Param("timestamp") Instant timestamp);

    @Modifying
    @Query("UPDATE UserCredential u SET u.accountLockedUntil = :lockedUntil WHERE u.userId = :userId")
    void lockAccount(@Param("userId") String userId, @Param("lockedUntil") Instant lockedUntil);

    @Modifying
    @Query("UPDATE UserCredential u SET u.failedLoginAttempts = 0, u.accountLockedUntil = null, u.lastFailedLoginAt = null WHERE u.userId = :userId")
    void resetFailedLoginAttempts(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE UserCredential u SET u.passwordHash = :hash WHERE u.userId = :userId")
    void updatePasswordHash(@Param("userId") String userId, @Param("hash") String hash);

    @Modifying
    @Query("UPDATE UserCredential u SET u.pinHash = :hash WHERE u.userId = :userId")
    void updatePinHash(@Param("userId") String userId, @Param("hash") String hash);

    @Modifying
    @Query("UPDATE UserCredential u SET u.lastLoginAt = :timestamp WHERE u.userId = :userId")
    void updateLastLoginAt(@Param("userId") String userId, @Param("timestamp") Instant timestamp);
}
