package com.microfinance.user.domain.model;

public enum KycStatus {
    NOT_STARTED,     // KYC not initiated
    PENDING,         // KYC documents submitted, under review
    APPROVED,        // KYC completed and approved
    REJECTED,        // KYC rejected, needs resubmission
    EXPIRED,         // KYC expired, needs renewal
    PARTIAL          // Some documents approved, others pending/rejected
}