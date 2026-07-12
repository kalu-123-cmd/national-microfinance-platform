package com.microfinance.user.domain.model;

public enum DocumentStatus {
    PENDING,    // Uploaded, waiting for verification
    APPROVED,   // Verified and approved
    REJECTED,   // Rejected, needs resubmission
    EXPIRED     // Document has expired
}