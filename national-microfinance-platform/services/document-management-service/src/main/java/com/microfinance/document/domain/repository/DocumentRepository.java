package com.microfinance.document.domain.repository;

import com.microfinance.document.domain.model.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<UserDocument, String> {
    List<UserDocument> findByUserId(String userId);
    List<UserDocument> findByUserIdAndDocumentType(String userId, String documentType);
    List<UserDocument> findByStatus(String status);
    long countByUserIdAndStatus(String userId, String status);
}
