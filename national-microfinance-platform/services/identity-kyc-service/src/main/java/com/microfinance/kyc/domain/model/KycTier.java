package com.microfinance.kyc.domain.model;

public enum KycTier {
    TIER_1,  // Basic: phone verification only, limited features
    TIER_2,  // Standard: ID + selfie, full features
    TIER_3   // Enhanced: ID + address + biometric, higher limits
}