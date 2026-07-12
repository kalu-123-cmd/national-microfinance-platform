package com.microfinance.document.dto;

import com.microfinance.document.domain.model.UserDocument;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class DocumentResponse {
    private String id;
    private String userId;
    private String fileName;
    private String documentType;
    private String status;
    private String mimeType;
    private long fileSize;
    private String storageUrl;
    private String ocrText;
    private Map<String, String> metadata;
    private LocalDateTime uploadedAt;
    private LocalDateTime processedAt;

    public static DocumentResponse from(UserDocument doc) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .userId(doc.getUserId())
                .fileName(doc.getOriginalFileName())
                .documentType(doc.getDocumentType())
                .status(doc.getStatus())
                .mimeType(doc.getMimeType())
                .fileSize(doc.getFileSize())
                .storageUrl(doc.getStorageUrl())
                .ocrText(doc.getOcrText())
                .metadata(doc.getMetadata())
                .uploadedAt(doc.getUploadedAt())
                .processedAt(doc.getProcessedAt())
                .build();
    }
}
