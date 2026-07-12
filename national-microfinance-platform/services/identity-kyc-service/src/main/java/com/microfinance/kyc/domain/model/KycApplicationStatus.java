package com.microfinance.kyc.domain.model;

public enum KycApplicationStatus {
    DRAFT,              // Created but not submitted
    SUBMITTED,          // Submitted for review
    DOCUMENTS_PENDING,  // Waiting for additional documents
    UNDER_REVIEW,       // Being reviewed by system
    PENDING_REVIEW,     // Manual review required
    APPROVED,           // Approved
    REJECTED,           // Rejected
    EXPIRED,            // Approval expired, renewal needed
    CANCELLED           // Cancelled by user
}