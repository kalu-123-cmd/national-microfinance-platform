package com.microfinance.user.dto;

import com.microfinance.user.domain.model.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    
    private String id;
    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private String nationalId;
    private String passportNumber;
    private String occupation;
    private Long monthlyIncome;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private UserStatus status;
    private KycStatus kycStatus;
    private String preferredLanguage;
    private AccountType accountType;
    private String profilePictureUrl;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    private Boolean termsAccepted;
    private Boolean privacyPolicyAccepted;
    private Boolean marketingConsent;
    private Instant lastLoginAt;
    private Instant lastActivityAt;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Additional computed fields
    private boolean active;
    private boolean kycCompleted;
    private boolean premiumUser;
}