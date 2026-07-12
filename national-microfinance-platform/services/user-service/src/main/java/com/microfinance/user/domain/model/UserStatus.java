package com.microfinance.user.domain.model;

public enum UserStatus {
    PENDING_VERIFICATION,  // Just registered, email/phone not verified
    ACTIVE,               // Verified and active
    SUSPENDED,           // Temporarily disabled by admin/system
    DEACTIVATED,         // User requested deactivation
    BLOCKED,             // Blocked due to security/fraud concerns
    CLOSED               // Account permanently closed
}