package com.microfinance.document.dto;

import lombok.Data;

@Data
public class DocumentUploadRequest {
    private String documentType; // KYC, LOAN_APPLICATION, CONTRACT, etc.
    private String description;
}
