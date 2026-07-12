package com.microfinance.user.dto;

import com.microfinance.user.domain.model.DocumentType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DocumentUploadRequest {

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @Size(max = 100, message = "Document number must not exceed 100 characters")
    private String documentNumber;

    @NotBlank(message = "Document name is required")
    @Size(max = 255, message = "Document name must not exceed 255 characters")
    private String documentName;

    private LocalDate expiryDate;
    private LocalDate issuedDate;

    @Size(max = 255, message = "Issuing authority must not exceed 255 characters")
    private String issuingAuthority;

    // File will be handled separately via multipart upload
}