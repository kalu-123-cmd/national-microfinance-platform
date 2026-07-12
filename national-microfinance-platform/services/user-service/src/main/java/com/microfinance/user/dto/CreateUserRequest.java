package com.microfinance.user.dto;

import com.microfinance.user.domain.model.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+251[79]\\d{8}$", message = "Phone number must be in format +251XXXXXXXXX")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be 2-100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be 2-100 characters")
    private String lastName;

    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    private String middleName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;
    private MaritalStatus maritalStatus;

    @Size(max = 50, message = "National ID must not exceed 50 characters")
    private String nationalId;

    @Size(max = 50, message = "Passport number must not exceed 50 characters")
    private String passportNumber;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    @Min(value = 0, message = "Monthly income must be non-negative")
    private Long monthlyIncome; // in cents

    @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
    private String addressLine2;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "Region must not exceed 100 characters")
    private String region;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @Size(max = 50, message = "Country must not exceed 50 characters")
    private String country;

    @Size(max = 10, message = "Preferred language must not exceed 10 characters")
    private String preferredLanguage;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @Size(max = 200, message = "Emergency contact name must not exceed 200 characters")
    private String emergencyContactName;

    @Pattern(regexp = "^\\+251[79]\\d{8}$", message = "Emergency contact phone must be in format +251XXXXXXXXX")
    private String emergencyContactPhone;

    @Size(max = 50, message = "Emergency contact relationship must not exceed 50 characters")
    private String emergencyContactRelationship;

    @NotNull(message = "Terms acceptance is required")
    @AssertTrue(message = "Terms and conditions must be accepted")
    private Boolean termsAccepted;

    @NotNull(message = "Privacy policy acceptance is required")
    @AssertTrue(message = "Privacy policy must be accepted")
    private Boolean privacyPolicyAccepted;

    private Boolean marketingConsent;

    private String createdBy;
}