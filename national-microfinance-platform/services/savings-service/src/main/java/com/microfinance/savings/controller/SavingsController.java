package com.microfinance.savings.controller;

import com.microfinance.savings.dto.*;
import com.microfinance.savings.service.SavingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/savings")
@RequiredArgsConstructor
public class SavingsController {

    private final SavingsService savingsService;

    // ── Health ───────────────────────────────────────────────────────────────

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "savings-service"));
    }

    // ── Savings Accounts ─────────────────────────────────────────────────────

    @PostMapping("/accounts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SavingsAccountResponse> createAccount(
            @Valid @RequestBody CreateSavingsAccountRequest request,
            @AuthenticationPrincipal UserDetails user) {
        request.setUserId(user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(savingsService.createAccount(request));
    }

    @GetMapping("/accounts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SavingsAccountResponse>> getMyAccounts(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(savingsService.getAccountsByUser(user.getUsername()));
    }

    @GetMapping("/accounts/{accountId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SavingsAccountResponse> getAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(savingsService.getAccount(accountId));
    }

    @PostMapping("/accounts/deposit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SavingsTransactionResponse> deposit(
            @Valid @RequestBody DepositRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(savingsService.deposit(request, user.getUsername()));
    }

    @PostMapping("/accounts/withdraw")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SavingsTransactionResponse> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(savingsService.withdraw(request, user.getUsername()));
    }

    @GetMapping("/accounts/{accountId}/transactions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<SavingsTransactionResponse>> getTransactions(
            @PathVariable String accountId, Pageable pageable) {
        return ResponseEntity.ok(savingsService.getTransactions(accountId, pageable));
    }

    // ── Fixed Deposits ────────────────────────────────────────────────────────

    @PostMapping("/fixed-deposits")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FixedDepositResponse> createFixedDeposit(
            @Valid @RequestBody CreateFixedDepositRequest request,
            @AuthenticationPrincipal UserDetails user) {
        request.setUserId(user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(savingsService.createFixedDeposit(request));
    }

    @GetMapping("/fixed-deposits")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FixedDepositResponse>> getMyFixedDeposits(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(savingsService.getFixedDepositsByUser(user.getUsername()));
    }

    @PostMapping("/fixed-deposits/{fdId}/close-premature")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FixedDepositResponse> closePremature(@PathVariable String fdId) {
        return ResponseEntity.ok(savingsService.closeFixedDepositPremature(fdId));
    }

    // ── Savings Goals ─────────────────────────────────────────────────────────

    @PostMapping("/goals")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SavingsGoalResponse> createGoal(
            @Valid @RequestBody CreateSavingsGoalRequest request,
            @AuthenticationPrincipal UserDetails user) {
        request.setUserId(user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(savingsService.createGoal(request));
    }

    @GetMapping("/goals")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SavingsGoalResponse>> getMyGoals(
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(savingsService.getGoalsByUser(user.getUsername()));
    }

    @GetMapping("/goals/{goalId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SavingsGoalResponse> getGoal(@PathVariable String goalId) {
        return ResponseEntity.ok(savingsService.getGoal(goalId));
    }

    @PostMapping("/goals/{goalId}/contribute")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SavingsGoalResponse> contribute(
            @PathVariable String goalId,
            @RequestParam BigDecimal amount,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(savingsService.contributeToGoal(goalId, amount, user.getUsername()));
    }
}
