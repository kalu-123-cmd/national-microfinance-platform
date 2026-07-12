package com.microfinance.user.service;

import com.microfinance.common.exception.*;
import com.microfinance.event.KafkaTopics;
import com.microfinance.event.user.UserRegisteredEvent;
import com.microfinance.event.user.UserSuspendedEvent;
import com.microfinance.user.domain.model.*;
import com.microfinance.user.domain.repository.*;
import com.microfinance.user.dto.*;
import com.microfinance.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserDocumentRepository documentRepository;
    private final UserMapper userMapper;
    private final KafkaOperations<String, Object> kafkaTemplate;
    private final FileStorageService fileStorageService;
    
    // Thread-safe counter for user ID generation
    private final AtomicInteger userCounter = new AtomicInteger(1);

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with phone: {}", request.getPhoneNumber());

        // Validate uniqueness
        validateUserUniqueness(request);

        // Generate user ID
        String userId = generateUserId();

        User user = User.builder()
            .id(userId)
            .phoneNumber(request.getPhoneNumber())
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .middleName(request.getMiddleName())
            .dateOfBirth(request.getDateOfBirth())
            .gender(request.getGender())
            .maritalStatus(request.getMaritalStatus())
            .nationalId(request.getNationalId())
            .passportNumber(request.getPassportNumber())
            .occupation(request.getOccupation())
            .monthlyIncome(request.getMonthlyIncome())
            .addressLine1(request.getAddressLine1())
            .addressLine2(request.getAddressLine2())
            .city(request.getCity())
            .region(request.getRegion())
            .postalCode(request.getPostalCode())
            .country(request.getCountry() != null ? request.getCountry() : "Ethiopia")
            .status(UserStatus.PENDING_VERIFICATION)
            .kycStatus(KycStatus.NOT_STARTED)
            .preferredLanguage(request.getPreferredLanguage() != null ? request.getPreferredLanguage() : "en")
            .accountType(request.getAccountType())
            .emergencyContactName(request.getEmergencyContactName())
            .emergencyContactPhone(request.getEmergencyContactPhone())
            .emergencyContactRelationship(request.getEmergencyContactRelationship())
            .termsAccepted(request.getTermsAccepted())
            .privacyPolicyAccepted(request.getPrivacyPolicyAccepted())
            .marketingConsent(request.getMarketingConsent() != null ? request.getMarketingConsent() : false)
            .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : "SYSTEM")
            .updatedBy(request.getCreatedBy() != null ? request.getCreatedBy() : "SYSTEM")
            .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        // Publish user registered event
        publishUserRegisteredEvent(savedUser);

        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(String userId) {
        User user = findUserById(userId);
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new NotFoundException("User not found with phone number: " + phoneNumber));
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(String userId, UpdateUserRequest request) {
        log.info("Updating user: {}", userId);

        User user = findUserById(userId);

        // Update only provided fields
        if (request.getEmail() != null) {
            validateEmailUniqueness(request.getEmail(), userId);
            user.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getMiddleName() != null) user.setMiddleName(request.getMiddleName());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getMaritalStatus() != null) user.setMaritalStatus(request.getMaritalStatus());
        if (request.getOccupation() != null) user.setOccupation(request.getOccupation());
        if (request.getMonthlyIncome() != null) user.setMonthlyIncome(request.getMonthlyIncome());
        if (request.getAddressLine1() != null) user.setAddressLine1(request.getAddressLine1());
        if (request.getAddressLine2() != null) user.setAddressLine2(request.getAddressLine2());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getRegion() != null) user.setRegion(request.getRegion());
        if (request.getPostalCode() != null) user.setPostalCode(request.getPostalCode());
        if (request.getCountry() != null) user.setCountry(request.getCountry());
        if (request.getPreferredLanguage() != null) user.setPreferredLanguage(request.getPreferredLanguage());
        if (request.getEmergencyContactName() != null) user.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getEmergencyContactPhone() != null) user.setEmergencyContactPhone(request.getEmergencyContactPhone());
        if (request.getEmergencyContactRelationship() != null) user.setEmergencyContactRelationship(request.getEmergencyContactRelationship());
        if (request.getMarketingConsent() != null) user.setMarketingConsent(request.getMarketingConsent());
        
        user.setUpdatedBy(request.getUpdatedBy() != null ? request.getUpdatedBy() : "SYSTEM");

        User savedUser = userRepository.save(user);
        log.info("User updated successfully: {}", userId);

        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String search, Pageable pageable) {
        return userRepository.searchUsers(search, pageable)
            .map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByStatus(UserStatus status) {
        return userRepository.findByStatus(status).stream()
            .map(userMapper::toResponse)
            .toList();
    }

    @Transactional
    public void updateUserStatus(String userId, UserStatus status, String updatedBy) {
        log.info("Updating user status: {} to {}", userId, status);
        
        User user = findUserById(userId);
        userRepository.updateUserStatus(userId, status, updatedBy);
        
        log.info("User status updated successfully: {} to {}", userId, status);
    }

    @Transactional
    public void activateUser(String userId) {
        log.info("Activating user: {}", userId);
        updateUserStatus(userId, UserStatus.ACTIVE, "SYSTEM");
    }

    @Transactional
    public void suspendUser(String userId, String reason) {
        log.info("Suspending user: {} for reason: {}", userId, reason);
        User user = findUserById(userId);
        updateUserStatus(userId, UserStatus.SUSPENDED, "ADMIN");
        publishUserSuspendedEvent(user, reason);
    }

    @Transactional
    public void updateLastActivity(String userId) {
        userRepository.updateLastActivityAt(userId, Instant.now());
    }

    @Transactional
    public void updateLastLogin(String userId) {
        userRepository.updateLastLoginAt(userId, Instant.now());
    }

    @Transactional
    public DocumentResponse uploadDocument(String userId, DocumentUploadRequest request, MultipartFile file) throws IOException {
        log.info("Uploading document for user: {}, type: {}", userId, request.getDocumentType());

        User user = findUserById(userId);

        // Validate file
        validateDocumentFile(file);

        // Check if document type already exists for user
        if (documentRepository.existsByUserIdAndDocumentType(userId, request.getDocumentType())) {
            throw new BusinessException("Document of type " + request.getDocumentType() + " already exists for user");
        }

        // Store file
        String filePath = fileStorageService.storeFile(file, userId, request.getDocumentType());

        UserDocument document = UserDocument.builder()
            .id(UUID.randomUUID().toString())
            .userId(userId)
            .documentType(request.getDocumentType())
            .documentNumber(request.getDocumentNumber())
            .documentName(request.getDocumentName())
            .filePath(filePath)
            .fileSize(file.getSize())
            .mimeType(file.getContentType())
            .status(DocumentStatus.PENDING)
            .expiryDate(request.getExpiryDate())
            .issuedDate(request.getIssuedDate())
            .issuingAuthority(request.getIssuingAuthority())
            .build();

        UserDocument savedDocument = documentRepository.save(document);
        log.info("Document uploaded successfully: {}", savedDocument.getId());

        // Update user KYC status if needed
        updateKycStatusBasedOnDocuments(userId);

        return userMapper.toDocumentResponse(savedDocument);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getUserDocuments(String userId) {
        return documentRepository.findByUserId(userId).stream()
            .map(userMapper::toDocumentResponse)
            .toList();
    }

    @Transactional
    public void verifyDocument(String documentId, String verifiedBy, String notes) {
        log.info("Verifying document: {}", documentId);

        UserDocument document = documentRepository.findById(documentId)
            .orElseThrow(() -> new NotFoundException("Document not found"));

        documentRepository.updateDocumentStatus(documentId, DocumentStatus.APPROVED, 
                                              notes, verifiedBy, Instant.now());

        // Update user KYC status
        updateKycStatusBasedOnDocuments(document.getUserId());

        log.info("Document verified successfully: {}", documentId);
    }

    @Transactional
    public void rejectDocument(String documentId, String reason) {
        log.info("Rejecting document: {} for reason: {}", documentId, reason);

        UserDocument document = documentRepository.findById(documentId)
            .orElseThrow(() -> new NotFoundException("Document not found"));

        documentRepository.rejectDocument(documentId, reason);

        // Update user KYC status
        updateKycStatusBasedOnDocuments(document.getUserId());

        log.info("Document rejected successfully: {}", documentId);
    }

    // ========== Private Helper Methods ==========

    private User findUserById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
    }

    private void validateUserUniqueness(CreateUserRequest request) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException("User already exists with phone number: " + request.getPhoneNumber());
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("User already exists with email: " + request.getEmail());
        }
        if (request.getNationalId() != null && userRepository.existsByNationalId(request.getNationalId())) {
            throw new BusinessException("User already exists with national ID: " + request.getNationalId());
        }
        if (request.getPassportNumber() != null && userRepository.existsByPassportNumber(request.getPassportNumber())) {
            throw new BusinessException("User already exists with passport number: " + request.getPassportNumber());
        }
    }

    private void validateEmailUniqueness(String email, String excludeUserId) {
        userRepository.findByEmail(email)
            .ifPresent(user -> {
                if (!user.getId().equals(excludeUserId)) {
                    throw new BusinessException("Email already in use by another user");
                }
            });
    }

    private String generateUserId() {
        int year = Year.now().getValue();
        int sequence = userCounter.getAndIncrement();
        return String.format("USER-%d-%06d", year, sequence);
    }

    private void publishUserRegisteredEvent(User user) {
        UserRegisteredEvent event = new UserRegisteredEvent(
            user.getId(),
            user.getPhoneNumber(),
            user.getFirstName(),
            user.getLastName()
        );
        event.setRegion(user.getRegion());
        event.setUserType(user.getAccountType() != null ? user.getAccountType().toString() : "BASIC");

        kafkaTemplate.send(KafkaTopics.USER_REGISTERED, event);
        log.info("Published user registered event for user: {}", user.getId());
    }

    private void publishUserSuspendedEvent(User user, String reason) {
        UserSuspendedEvent event = new UserSuspendedEvent(
            user.getId(),
            user.getPhoneNumber(),
            user.getEmail(),
            reason
        );
        kafkaTemplate.send(KafkaTopics.USER_SUSPENDED, event);
        log.info("Published user suspended event for user: {}", user.getId());
    }

    private void validateDocumentFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("File cannot be empty");
        }

        // Check file size (max 10MB)
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new BusinessException("File size cannot exceed 10MB");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
            throw new BusinessException("Only image and PDF files are allowed");
        }
    }

    private void updateKycStatusBasedOnDocuments(String userId) {
        long approvedDocuments = documentRepository.countApprovedDocumentsByUser(userId);
        
        KycStatus newStatus;
        if (approvedDocuments == 0) {
            newStatus = KycStatus.NOT_STARTED;
        } else if (approvedDocuments >= 2) { // At least ID and address proof
            newStatus = KycStatus.APPROVED;
        } else {
            newStatus = KycStatus.PARTIAL;
        }

        userRepository.updateKycStatus(userId, newStatus, "SYSTEM");
        log.info("Updated KYC status for user {} to {}", userId, newStatus);
    }
}