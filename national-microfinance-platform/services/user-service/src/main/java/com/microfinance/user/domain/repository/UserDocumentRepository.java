package com.microfinance.user.domain.repository;

import com.microfinance.user.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDocumentRepository extends JpaRepository<UserDocument, String> {

    List<UserDocument> findByUserId(String userId);
    
    List<UserDocument> findByUserIdAndStatus(String userId, DocumentStatus status);
    
    Optional<UserDocument> findByUserIdAndDocumentType(String userId, DocumentType documentType);
    
    List<UserDocument> findByStatus(DocumentStatus status);
    
    List<UserDocument> findByDocumentType(DocumentType documentType);

    @Query("SELECT d FROM UserDocument d WHERE d.userId = :userId AND d.documentType IN :types")
    List<UserDocument> findByUserIdAndDocumentTypes(@Param("userId") String userId, @Param("types") List<DocumentType> types);

    @Query("SELECT d FROM UserDocument d WHERE d.expiryDate BETWEEN :fromDate AND :toDate")
    List<UserDocument> findDocumentsExpiringBetween(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query("SELECT d FROM UserDocument d WHERE d.expiryDate <= :date AND d.status = 'APPROVED'")
    List<UserDocument> findExpiredDocuments(@Param("date") LocalDate date);

    @Query("SELECT COUNT(d) FROM UserDocument d WHERE d.userId = :userId AND d.status = 'APPROVED'")
    long countApprovedDocumentsByUser(@Param("userId") String userId);

    @Query("SELECT COUNT(d) FROM UserDocument d WHERE d.status = :status")
    long countByStatus(@Param("status") DocumentStatus status);

    @Modifying
    @Query("UPDATE UserDocument d SET d.status = :status, d.verificationNotes = :notes, d.verifiedBy = :verifiedBy, d.verifiedAt = :verifiedAt WHERE d.id = :documentId")
    void updateDocumentStatus(@Param("documentId") String documentId, 
                             @Param("status") DocumentStatus status,
                             @Param("notes") String notes,
                             @Param("verifiedBy") String verifiedBy,
                             @Param("verifiedAt") Instant verifiedAt);

    @Modifying
    @Query("UPDATE UserDocument d SET d.status = 'REJECTED', d.rejectionReason = :reason WHERE d.id = :documentId")
    void rejectDocument(@Param("documentId") String documentId, @Param("reason") String reason);

    boolean existsByUserIdAndDocumentType(String userId, DocumentType documentType);

    void deleteByUserId(String userId);
}