package com.microfinance.user.dto;

import com.microfinance.user.domain.model.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DocumentResponse {
    
    private String id;
    private String userId;
    private DocumentType documentType;
    private String documentNumber;
    private String documentName;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private DocumentStatus status;
    private LocalDate expiryDate;
    private LocalDate issuedDate;
    private String issuingAuthority;
    private String verificationNotes;
    private String verifiedBy;
    private Instant verifiedAt;
    private String rejectionReason;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Computed fields
    private boolean verified;
    private boolean expired;
    private boolean needsRenewal;
}