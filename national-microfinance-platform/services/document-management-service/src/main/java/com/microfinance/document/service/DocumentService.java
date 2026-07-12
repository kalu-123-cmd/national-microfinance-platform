package com.microfinance.document.service;

import com.microfinance.document.domain.model.UserDocument;
import com.microfinance.document.domain.repository.DocumentRepository;
import com.microfinance.document.dto.DocumentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    private static final String UPLOAD_DIR = System.getProperty("java.io.tmpdir") + "/documents/";

    public DocumentResponse uploadDocument(String userId, String documentType,
                                            MultipartFile file, String uploadedBy) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR + userId);
        Files.createDirectories(uploadPath);

        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(storedFileName);
        Files.copy(file.getInputStream(), filePath);

        UserDocument doc = UserDocument.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .fileName(storedFileName)
                .originalFileName(file.getOriginalFilename())
                .mimeType(file.getContentType())
                .fileSize(file.getSize())
                .documentType(documentType)
                .storageUrl(filePath.toString())
                .status("UPLOADED")
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(uploadedBy)
                .metadata(Map.of("originalName", file.getOriginalFilename() != null ? file.getOriginalFilename() : ""))
                .fileType(detectFileType(file.getContentType()))
                .build();

        documentRepository.save(doc);
        log.info("Document {} uploaded for user {} (type: {})", doc.getId(), userId, documentType);
        return DocumentResponse.from(doc);
    }

    public List<DocumentResponse> getDocumentsByUser(String userId) {
        return documentRepository.findByUserId(userId).stream()
                .map(DocumentResponse::from).collect(Collectors.toList());
    }

    public List<DocumentResponse> getDocumentsByUserAndType(String userId, String documentType) {
        return documentRepository.findByUserIdAndDocumentType(userId, documentType).stream()
                .map(DocumentResponse::from).collect(Collectors.toList());
    }

    public DocumentResponse getDocument(String documentId) {
        return DocumentResponse.from(documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId)));
    }

    public DocumentResponse verifyDocument(String documentId, String verifiedBy) {
        UserDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        doc.setStatus("VERIFIED");
        doc.setVerifiedBy(verifiedBy);
        doc.setProcessedAt(LocalDateTime.now());
        documentRepository.save(doc);
        log.info("Document {} verified by {}", documentId, verifiedBy);
        return DocumentResponse.from(doc);
    }

    public DocumentResponse rejectDocument(String documentId, String reason, String rejectedBy) {
        UserDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        doc.setStatus("REJECTED");
        doc.setRejectionReason(reason);
        doc.setVerifiedBy(rejectedBy);
        doc.setProcessedAt(LocalDateTime.now());
        documentRepository.save(doc);
        log.info("Document {} rejected: {}", documentId, reason);
        return DocumentResponse.from(doc);
    }

    public void deleteDocument(String documentId, String requestedBy) {
        UserDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        try {
            Files.deleteIfExists(Paths.get(doc.getStorageUrl()));
        } catch (IOException e) {
            log.warn("Could not delete file {}: {}", doc.getStorageUrl(), e.getMessage());
        }
        documentRepository.delete(doc);
        log.info("Document {} deleted by {}", documentId, requestedBy);
    }

    public List<DocumentResponse> getPendingDocuments() {
        return documentRepository.findByStatus("UPLOADED").stream()
                .map(DocumentResponse::from).collect(Collectors.toList());
    }

    private String detectFileType(String mimeType) {
        if (mimeType == null) return "UNKNOWN";
        if (mimeType.startsWith("image/")) return "IMAGE";
        if (mimeType.equals("application/pdf")) return "PDF";
        if (mimeType.contains("word")) return "WORD";
        if (mimeType.contains("excel") || mimeType.contains("spreadsheet")) return "EXCEL";
        return "OTHER";
    }
}
