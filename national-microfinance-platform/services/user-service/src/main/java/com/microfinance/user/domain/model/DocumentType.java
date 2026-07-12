package com.microfinance.user.domain.model;

public enum DocumentType {
    // Identity Documents
    NATIONAL_ID,
    PASSPORT,
    DRIVERS_LICENSE,
    VOTER_ID,
    
    // Address Proof
    UTILITY_BILL,
    BANK_STATEMENT,
    RENTAL_AGREEMENT,
    GOVERNMENT_LETTER,
    
    // Income Proof
    SALARY_CERTIFICATE,
    EMPLOYMENT_LETTER,
    BUSINESS_REGISTRATION,
    TAX_RETURN,
    
    // Photos
    PROFILE_PHOTO,
    SIGNATURE,
    
    // Other KYC Documents
    BIRTH_CERTIFICATE,
    MARRIAGE_CERTIFICATE,
    EDUCATIONAL_CERTIFICATE,
    
    // Business Documents (for merchant accounts)
    BUSINESS_LICENSE,
    TAX_ID_CERTIFICATE,
    MEMORANDUM_OF_ASSOCIATION
}