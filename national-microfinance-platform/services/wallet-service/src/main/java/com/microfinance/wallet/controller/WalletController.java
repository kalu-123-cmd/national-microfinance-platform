package com.microfinance.wallet.controller;

import com.microfinance.common.dto.*;
import com.microfinance.wallet.dto.*;
import com.microfinance.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@Tag(name = "Wallet", description = "Wallet and transaction management")
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    @Operation(summary = "Create wallet")
    public ResponseEntity<ApiResponse<WalletResponse>> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(walletService.createWallet(request), "Wallet created"));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get wallet by user ID")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success(walletService.getWalletByUserId(userId), "Retrieved"));
    }

    @GetMapping("/number/{walletNumber}")
    @Operation(summary = "Get wallet by wallet number")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletByNumber(@PathVariable String walletNumber) {
        return ResponseEntity.ok(ApiResponse.success(walletService.getWalletByNumber(walletNumber), "Retrieved"));
    }

    @PostMapping("/credit")
    @Operation(summary = "Credit wallet")
    public ResponseEntity<ApiResponse<TransactionResponse>> credit(@Valid @RequestBody CreditWalletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(walletService.credit(request), "Credited"));
    }

    @PostMapping("/debit")
    @Operation(summary = "Debit wallet")
    public ResponseEntity<ApiResponse<TransactionResponse>> debit(@Valid @RequestBody DebitWalletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(walletService.debit(request), "Debited"));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer between wallets")
    public ResponseEntity<ApiResponse<TransferResponse>> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(ApiResponse.success(walletService.transfer(request), "Transfer completed"));
    }

    @GetMapping("/{walletId}/transactions")
    @Operation(summary = "Get wallet transactions")
    public ResponseEntity<ApiResponse<PageResponse<TransactionResponse>>> getTransactions(
            @PathVariable String walletId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
            PageResponse.of(walletService.getTransactions(walletId, pageable)), "Retrieved"));
    }

    @PostMapping("/{walletId}/freeze")
    @Operation(summary = "Freeze wallet")
    public ResponseEntity<ApiResponse<Void>> freeze(@PathVariable String walletId, @RequestParam String reason) {
        walletService.freezeWallet(walletId, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "Wallet frozen"));
    }

    @PostMapping("/{walletId}/unfreeze")
    @Operation(summary = "Unfreeze wallet")
    public ResponseEntity<ApiResponse<Void>> unfreeze(@PathVariable String walletId) {
        walletService.unfreezeWallet(walletId);
        return ResponseEntity.ok(ApiResponse.success(null, "Wallet unfrozen"));
    }
}