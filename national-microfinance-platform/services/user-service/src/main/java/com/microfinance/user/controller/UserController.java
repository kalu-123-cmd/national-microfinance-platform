package com.microfinance.user.controller;

import com.microfinance.common.dto.ApiResponse;
import com.microfinance.common.dto.PageResponse;
import com.microfinance.user.domain.model.UserStatus;
import com.microfinance.user.dto.*;
import com.microfinance.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User profile and document management endpoints")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user profile")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "User created successfully"));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve user profile by user ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "User retrieved successfully"));
    }

    @GetMapping("/phone/{phoneNumber}")
    @Operation(summary = "Get user by phone", description = "Retrieve user profile by phone number")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByPhone(@PathVariable String phoneNumber) {
        UserResponse response = userService.getUserByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success(response, "User retrieved successfully"));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve user profile by email address")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response, "User retrieved successfully"));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user", description = "Update user profile information")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "User updated successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve paginated list of all users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(Pageable pageable) {
        Page<UserResponse> page = userService.getAllUsers(pageable);
        PageResponse<UserResponse> response = PageResponse.of(page);
        return ResponseEntity.ok(ApiResponse.success(response, "Users retrieved successfully"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by name, phone, or email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> searchUsers(
            @RequestParam String query,
            Pageable pageable) {
        Page<UserResponse> page = userService.searchUsers(query, pageable);
        PageResponse<UserResponse> response = PageResponse.of(page);
        return ResponseEntity.ok(ApiResponse.success(response, "Search results retrieved successfully"));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get users by status", description = "Retrieve users with specific status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByStatus(@PathVariable UserStatus status) {
        List<UserResponse> response = userService.getUsersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(response, "Users retrieved successfully"));
    }

    @PutMapping("/{userId}/status")
    @Operation(summary = "Update user status", description = "Update user account status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable String userId,
            @RequestParam UserStatus status,
            @RequestParam(defaultValue = "ADMIN") String updatedBy) {
        userService.updateUserStatus(userId, status, updatedBy);
        return ResponseEntity.ok(ApiResponse.success(null, "User status updated successfully"));
    }

    @PutMapping("/{userId}/activate")
    @Operation(summary = "Activate user", description = "Activate user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable String userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "User activated successfully"));
    }

    @PutMapping("/{userId}/suspend")
    @Operation(summary = "Suspend user", description = "Suspend user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> suspendUser(
            @PathVariable String userId,
            @RequestParam String reason) {
        userService.suspendUser(userId, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "User suspended successfully"));
    }

    @PutMapping("/{userId}/activity")
    @Operation(summary = "Update last activity", description = "Update user's last activity timestamp")
    public ResponseEntity<ApiResponse<Void>> updateLastActivity(@PathVariable String userId) {
        userService.updateLastActivity(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Last activity updated"));
    }

    @PutMapping("/{userId}/login")
    @Operation(summary = "Update last login", description = "Update user's last login timestamp")
    public ResponseEntity<ApiResponse<Void>> updateLastLogin(@PathVariable String userId) {
        userService.updateLastLogin(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Last login updated"));
    }

    // Document Management Endpoints

    @PostMapping("/{userId}/documents")
    @Operation(summary = "Upload document", description = "Upload KYC document for user")
    public ResponseEntity<ApiResponse<DocumentResponse>> uploadDocument(
            @PathVariable String userId,
            @Valid @ModelAttribute DocumentUploadRequest request,
            @RequestParam("file") MultipartFile file) throws IOException {
        DocumentResponse response = userService.uploadDocument(userId, request, file);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Document uploaded successfully"));
    }

    @GetMapping("/{userId}/documents")
    @Operation(summary = "Get user documents", description = "Retrieve all documents for a user")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getUserDocuments(@PathVariable String userId) {
        List<DocumentResponse> response = userService.getUserDocuments(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Documents retrieved successfully"));
    }

    @PutMapping("/documents/{documentId}/verify")
    @Operation(summary = "Verify document", description = "Verify a user document")
    @PreAuthorize("hasRole('ADMIN') or hasRole('KYC_OFFICER')")
    public ResponseEntity<ApiResponse<Void>> verifyDocument(
            @PathVariable String documentId,
            @RequestParam String verifiedBy,
            @RequestParam(required = false) String notes) {
        userService.verifyDocument(documentId, verifiedBy, notes);
        return ResponseEntity.ok(ApiResponse.success(null, "Document verified successfully"));
    }

    @PutMapping("/documents/{documentId}/reject")
    @Operation(summary = "Reject document", description = "Reject a user document")
    @PreAuthorize("hasRole('ADMIN') or hasRole('KYC_OFFICER')")
    public ResponseEntity<ApiResponse<Void>> rejectDocument(
            @PathVariable String documentId,
            @RequestParam String reason) {
        userService.rejectDocument(documentId, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "Document rejected successfully"));
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if user service is running")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("OK", "User service is healthy"));
    }
}