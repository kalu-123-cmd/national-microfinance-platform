package com.microfinance.kyc.domain.model;

public enum CheckType {
    ID_DOCUMENT_VERIFICATION,   // OCR + document authenticity
    FACE_MATCH,                  // Selfie vs ID document face match
    LIVENESS_DETECTION,          // Anti-spoofing liveness check
    ADDRESS_VERIFICATION,        // Address proof document check
    PEP_SCREENING,               // Politically Exposed Person check
    SANCTIONS_SCREENING,         // UN/OFAC sanctions check
    ADVERSE_MEDIA,               // Negative news screening
    PHONE_VERIFICATION,          // OTP verification
    EMAIL_VERIFICATION,          // Email OTP verification
    CREDIT_CHECK,                // Basic credit check
    DUPLICATE_DETECTION,         // Duplicate account check
    AGE_VERIFICATION             // Age verification (18+)
}