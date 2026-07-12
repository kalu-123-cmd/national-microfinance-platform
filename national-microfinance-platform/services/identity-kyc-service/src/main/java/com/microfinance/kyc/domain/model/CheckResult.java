package com.microfinance.kyc.domain.model;

public enum CheckResult {
    PASSED,           // Check passed
    FAILED,           // Check failed
    PENDING,          // Awaiting external response
    ERROR,            // Technical error during check
    MANUAL_REVIEW,    // Needs manual review
    SKIPPED           // Check was skipped (optional check)
}