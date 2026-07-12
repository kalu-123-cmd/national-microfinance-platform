package com.microfinance.user.domain.repository;

import com.microfinance.user.domain.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByPhoneNumber(String phoneNumber);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByNationalId(String nationalId);
    
    Optional<User> findByPassportNumber(String passportNumber);

    boolean existsByPhoneNumber(String phoneNumber);
    
    boolean existsByEmail(String email);
    
    boolean existsByNationalId(String nationalId);
    
    boolean existsByPassportNumber(String passportNumber);

    List<User> findByStatus(UserStatus status);
    
    List<User> findByKycStatus(KycStatus kycStatus);
    
    List<User> findByAccountType(AccountType accountType);

    Page<User> findByStatusAndKycStatus(UserStatus status, KycStatus kycStatus, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %:search% OR u.lastName LIKE %:search% OR u.phoneNumber LIKE %:search% OR u.email LIKE %:search%")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.region = :region AND u.status = :status")
    List<User> findByRegionAndStatus(@Param("region") String region, @Param("status") UserStatus status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(@Param("status") UserStatus status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.kycStatus = :kycStatus")
    long countByKycStatus(@Param("kycStatus") KycStatus kycStatus);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :fromDate")
    long countNewUsersFromDate(@Param("fromDate") Instant fromDate);

    @Modifying
    @Query("UPDATE User u SET u.status = :status, u.updatedBy = :updatedBy WHERE u.id = :userId")
    void updateUserStatus(@Param("userId") String userId, @Param("status") UserStatus status, @Param("updatedBy") String updatedBy);

    @Modifying
    @Query("UPDATE User u SET u.kycStatus = :kycStatus, u.updatedBy = :updatedBy WHERE u.id = :userId")
    void updateKycStatus(@Param("userId") String userId, @Param("kycStatus") KycStatus kycStatus, @Param("updatedBy") String updatedBy);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :timestamp WHERE u.id = :userId")
    void updateLastLoginAt(@Param("userId") String userId, @Param("timestamp") Instant timestamp);

    @Modifying
    @Query("UPDATE User u SET u.lastActivityAt = :timestamp WHERE u.id = :userId")
    void updateLastActivityAt(@Param("userId") String userId, @Param("timestamp") Instant timestamp);

    @Modifying
    @Query("UPDATE User u SET u.profilePictureUrl = :url WHERE u.id = :userId")
    void updateProfilePicture(@Param("userId") String userId, @Param("url") String url);

    @Modifying
    @Query("UPDATE User u SET u.deviceTokens = :tokens WHERE u.id = :userId")
    void updateDeviceTokens(@Param("userId") String userId, @Param("tokens") String tokens);

    // Analytics queries
    @Query("SELECT u.region, COUNT(u) FROM User u WHERE u.status = 'ACTIVE' GROUP BY u.region")
    List<Object[]> getUserCountByRegion();

    @Query("SELECT u.accountType, COUNT(u) FROM User u GROUP BY u.accountType")
    List<Object[]> getUserCountByAccountType();

    @Query("SELECT DATE(u.createdAt) as date, COUNT(u) FROM User u WHERE u.createdAt >= :fromDate GROUP BY DATE(u.createdAt) ORDER BY date")
    List<Object[]> getUserRegistrationTrends(@Param("fromDate") Instant fromDate);
}