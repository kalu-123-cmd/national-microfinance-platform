package com.microfinance.user.dto;

import com.microfinance.user.domain.model.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UpdateUserRequest {

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 2, max = 100, message = "First name must be 2-100 characters")
    private String firstName;

    @Size(min = 2, max = 100, message = "Last name must be 2-100 characters")
    private String lastName;

    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    private String middleName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;
    private MaritalStatus maritalStatus;

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

    @Size(max = 200, message = "Emergency contact name must not exceed 200 characters")
    private String emergencyContactName;

    @Pattern(regexp = "^\\+251[79]\\d{8}$", message = "Emergency contact phone must be in format +251XXXXXXXXX")
    private String emergencyContactPhone;

    @Size(max = 50, message = "Emergency contact relationship must not exceed 50 characters")
    private String emergencyContactRelationship;

    private Boolean marketingConsent;

    private String updatedBy;
}