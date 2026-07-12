package com.microfinance.auth.controller;

import com.microfinance.auth.dto.*;
import com.microfinance.auth.service.AuthService;
import com.microfinance.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register user credentials", description = "Create password and PIN for a new user")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterCredentialRequest request) {
        authService.registerCredential(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(null, "Credential registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with password or PIN", description = "Authenticate user and return JWT tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        if (request.getIpAddress() == null) {
            request.setIpAddress(getClientIpAddress(httpRequest));
        }
        
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/otp/send")
    @Operation(summary = "Send OTP", description = "Send OTP code to phone or email")
    public ResponseEntity<ApiResponse<Void>> sendOtp(@Valid @RequestBody OtpSendRequest request) {
        authService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.success(null, "OTP sent successfully"));
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Verify OTP", description = "Verify OTP code and optionally return tokens for login")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        AuthResponse response = authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success(response, "OTP verified successfully"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Get new access token using refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }

    @PostMapping("/password/change")
    @Operation(summary = "Change password", description = "Change user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @PostMapping("/pin/change")
    @Operation(summary = "Change PIN", description = "Change user PIN")
    public ResponseEntity<ApiResponse<Void>> changePin(@Valid @RequestBody ChangePinRequest request) {
        authService.changePin(request);
        return ResponseEntity.ok(ApiResponse.success(null, "PIN changed successfully"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Revoke refresh token and logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestParam String userId,
            @RequestParam(required = false) String refreshToken) {
        authService.logout(userId, refreshToken);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @PostMapping("/sessions/revoke-all")
    @Operation(summary = "Revoke all sessions", description = "Revoke all refresh tokens for a user")
    public ResponseEntity<ApiResponse<Void>> revokeAllSessions(@RequestParam String userId) {
        authService.revokeAllSessions(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "All sessions revoked successfully"));
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if auth service is running")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("OK", "Auth service is healthy"));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
