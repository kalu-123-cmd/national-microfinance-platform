package com.microfinance.user.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    private String id;  // USER-YYYY-XXXXXX format

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "email", unique = true, length = 255)
    private String email;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    private MaritalStatus maritalStatus;

    @Column(name = "national_id", length = 50)
    private String nationalId;

    @Column(name = "passport_number", length = 50)
    private String passportNumber;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "monthly_income")
    private Long monthlyIncome; // in cents

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "region", length = 100)
    private String region;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "country", length = 50)
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status", nullable = false, length = 20)
    private KycStatus kycStatus;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Column(name = "emergency_contact_name", length = 200)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "emergency_contact_relationship", length = 50)
    private String emergencyContactRelationship;

    @Column(name = "terms_accepted")
    private Boolean termsAccepted;

    @Column(name = "privacy_policy_accepted")
    private Boolean privacyPolicyAccepted;

    @Column(name = "marketing_consent")
    private Boolean marketingConsent;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "last_activity_at")
    private Instant lastActivityAt;

    @Column(name = "device_tokens", columnDefinition = "TEXT")
    private String deviceTokens; // JSON array for push notifications

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    // Helper methods
    public String getFullName() {
        StringBuilder name = new StringBuilder(firstName);
        if (middleName != null && !middleName.trim().isEmpty()) {
            name.append(" ").append(middleName);
        }
        name.append(" ").append(lastName);
        return name.toString();
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean isKycCompleted() {
        return kycStatus == KycStatus.APPROVED;
    }

    public boolean isPremiumUser() {
        return accountType == AccountType.PREMIUM;
    }
}