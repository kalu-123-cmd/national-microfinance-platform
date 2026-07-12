package com.microfinance.document.controller;

import com.microfinance.document.dto.DocumentResponse;
import com.microfinance.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> upload(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(documentService.uploadDocument(userId, documentType, file, userId));
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getUserDocuments(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String type) {
        if (type != null) {
            return ResponseEntity.ok(documentService.getDocumentsByUserAndType(userId, type));
        }
        return ResponseEntity.ok(documentService.getDocumentsByUser(userId));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> getDocument(@PathVariable String documentId) {
        return ResponseEntity.ok(documentService.getDocument(documentId));
    }

    @PutMapping("/{documentId}/verify")
    public ResponseEntity<DocumentResponse> verify(
            @PathVariable String documentId,
            @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(documentService.verifyDocument(documentId, adminId));
    }

    @PutMapping("/{documentId}/reject")
    public ResponseEntity<DocumentResponse> reject(
            @PathVariable String documentId,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") String adminId) {
        return ResponseEntity.ok(documentService.rejectDocument(documentId, body.get("reason"), adminId));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> delete(
            @PathVariable String documentId,
            @RequestHeader("X-User-Id") String userId) {
        documentService.deleteDocument(documentId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<DocumentResponse>> getPending() {
        return ResponseEntity.ok(documentService.getPendingDocuments());
    }
}
