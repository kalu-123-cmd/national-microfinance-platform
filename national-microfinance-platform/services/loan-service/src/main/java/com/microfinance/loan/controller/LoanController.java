package com.microfinance.loan.controller;

import com.microfinance.common.dto.*;
import com.microfinance.loan.domain.model.LoanStatus;
import com.microfinance.loan.dto.*;
import com.microfinance.loan.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Tag(name = "Loan", description = "Loan management endpoints")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @Operation(summary = "Apply for a loan")
    public ResponseEntity<ApiResponse<LoanResponse>> apply(@Valid @RequestBody LoanApplicationRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(loanService.applyForLoan(req), "Loan application submitted"));
    }

    @GetMapping("/{loanId}")
    @Operation(summary = "Get loan details")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoan(@PathVariable String loanId) {
        return ResponseEntity.ok(ApiResponse.success(loanService.getLoan(loanId), "Retrieved"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user loans")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getUserLoans(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(loanService.getUserLoans(userId), "Retrieved"));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get loans by status (admin)")
    public ResponseEntity<ApiResponse<PageResponse<LoanResponse>>> getByStatus(
            @PathVariable LoanStatus status, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
            PageResponse.of(loanService.getLoansByStatus(status, pageable)), "Retrieved"));
    }

    @PutMapping("/{loanId}/approve")
    @Operation(summary = "Approve loan (loan officer)")
    public ResponseEntity<ApiResponse<LoanResponse>> approve(
            @PathVariable String loanId, @Valid @RequestBody ApproveLoanRequest req) {
        return ResponseEntity.ok(ApiResponse.success(loanService.approveLoan(loanId, req), "Approved"));
    }

    @PutMapping("/{loanId}/reject")
    @Operation(summary = "Reject loan")
    public ResponseEntity<ApiResponse<LoanResponse>> reject(
            @PathVariable String loanId, @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success(loanService.rejectLoan(loanId, reason), "Rejected"));
    }

    @PutMapping("/{loanId}/disburse")
    @Operation(summary = "Disburse approved loan")
    public ResponseEntity<ApiResponse<LoanResponse>> disburse(@PathVariable String loanId) {
        return ResponseEntity.ok(ApiResponse.success(loanService.disburseLoan(loanId), "Disbursed"));
    }

    @PostMapping("/repay")
    @Operation(summary = "Make a repayment")
    public ResponseEntity<ApiResponse<RepaymentResponse>> repay(@Valid @RequestBody RepaymentRequest req) {
        return ResponseEntity.ok(ApiResponse.success(loanService.makeRepayment(req), "Repayment recorded"));
    }

    @GetMapping("/{loanId}/schedule")
    @Operation(summary = "Get repayment schedule")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedule(@PathVariable String loanId) {
        return ResponseEntity.ok(ApiResponse.success(loanService.getRepaymentSchedule(loanId), "Retrieved"));
    }
}