package com.microfinance.document.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "documents")
public class UserDocument {
    @Id
    private String id;

    @Indexed
    private String userId;

    private String fileName;
    private String originalFileName;
    private String fileType;       // PDF, IMAGE, WORD, EXCEL, OTHER
    private String mimeType;
    private long fileSize;
    private String storageUrl;     // local path / S3 key / MinIO path

    @Indexed
    private String documentType;   // KYC, LOAN_APPLICATION, CONTRACT, IDENTITY, etc.

    @Indexed
    private String status;         // UPLOADED, PROCESSING, VERIFIED, REJECTED

    private String ocrText;
    private Map<String, String> metadata;
    private LocalDateTime uploadedAt;
    private LocalDateTime processedAt;
    private String uploadedBy;
    private String verifiedBy;
    private String rejectionReason;
}
