package com.microfinance.wallet.service;

import com.microfinance.common.exception.*;
import com.microfinance.wallet.domain.model.*;
import com.microfinance.wallet.domain.repository.*;
import com.microfinance.wallet.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {
        if (walletRepository.existsByUserId(request.getUserId())) {
            throw new BusinessException("Wallet already exists for user");
        }

        String walletNumber = generateWalletNumber();
        Wallet wallet = Wallet.builder()
            .id(UUID.randomUUID().toString())
            .userId(request.getUserId())
            .walletNumber(walletNumber)
            .balance(BigDecimal.ZERO)
            .reservedBalance(BigDecimal.ZERO)
            .currency("ETB")
            .status(WalletStatus.ACTIVE)
            .walletType(request.getWalletType() != null ? request.getWalletType() : WalletType.PERSONAL)
            .dailyLimit(new BigDecimal("50000.00"))
            .monthlyLimit(new BigDecimal("500000.00"))
            .singleTxLimit(new BigDecimal("20000.00"))
            .dailySpent(BigDecimal.ZERO)
            .monthlySpent(BigDecimal.ZERO)
            .totalCredited(BigDecimal.ZERO)
            .totalDebited(BigDecimal.ZERO)
            .dailyResetAt(Instant.now().plus(1, ChronoUnit.DAYS))
            .monthlyResetAt(Instant.now().plus(30, ChronoUnit.DAYS))
            .build();

        Wallet saved = walletRepository.save(wallet);
        log.info("Wallet created: {} for user: {}", walletNumber, request.getUserId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public WalletResponse getWalletByUserId(String userId) {
        return toResponse(walletRepository.findByUserId(userId)
            .orElseThrow(() -> new NotFoundException("Wallet not found for user: " + userId)));
    }

    @Transactional(readOnly = true)
    public WalletResponse getWalletByNumber(String walletNumber) {
        return toResponse(walletRepository.findByWalletNumber(walletNumber)
            .orElseThrow(() -> new NotFoundException("Wallet not found: " + walletNumber)));
    }

    @Transactional
    public TransactionResponse credit(CreditWalletRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new NotFoundException("Wallet not found"));

        if (!wallet.isActive()) throw new BusinessException("Wallet is not active");

        String ref = generateReference("CRD");
        BigDecimal before = wallet.getBalance();

        int rows = walletRepository.creditWallet(request.getWalletId(), request.getAmount());
        if (rows == 0) throw new BusinessException("Credit operation failed");

        wallet = walletRepository.findById(request.getWalletId()).orElseThrow();

        Transaction tx = Transaction.builder()
            .id(UUID.randomUUID().toString())
            .reference(ref)
            .walletId(wallet.getId())
            .userId(wallet.getUserId())
            .type(request.getTransactionType() != null ? request.getTransactionType() : TransactionType.DEPOSIT)
            .direction(TransactionDirection.CREDIT)
            .status(TransactionStatus.COMPLETED)
            .amount(request.getAmount())
            .fee(BigDecimal.ZERO)
            .currency(wallet.getCurrency())
            .balanceBefore(before)
            .balanceAfter(wallet.getBalance())
            .description(request.getDescription())
            .channel(request.getChannel())
            .build();

        Transaction saved = transactionRepository.save(tx);
        kafkaTemplate.send("wallet-events", new WalletCreditedMsg(wallet.getUserId(), wallet.getId(), request.getAmount().toString(), ref));
        log.info("Wallet credited: {} ETB to {} | ref: {}", request.getAmount(), wallet.getWalletNumber(), ref);
        return toTxResponse(saved);
    }

    @Transactional
    public TransactionResponse debit(DebitWalletRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new NotFoundException("Wallet not found"));

        if (!wallet.isActive()) throw new BusinessException("Wallet is not active");
        if (!wallet.hasEnoughBalance(request.getAmount()))
            throw new BusinessException("Insufficient balance");
        if (request.getAmount().compareTo(wallet.getSingleTxLimit()) > 0)
            throw new BusinessException("Amount exceeds single transaction limit");

        String ref = generateReference("DBT");
        BigDecimal before = wallet.getBalance();

        int rows = walletRepository.debitWallet(request.getWalletId(), request.getAmount());
        if (rows == 0) throw new BusinessException("Debit failed — insufficient balance or wallet inactive");

        wallet = walletRepository.findById(request.getWalletId()).orElseThrow();

        Transaction tx = Transaction.builder()
            .id(UUID.randomUUID().toString())
            .reference(ref)
            .walletId(wallet.getId())
            .userId(wallet.getUserId())
            .type(request.getTransactionType() != null ? request.getTransactionType() : TransactionType.WITHDRAWAL)
            .direction(TransactionDirection.DEBIT)
            .status(TransactionStatus.COMPLETED)
            .amount(request.getAmount())
            .fee(BigDecimal.ZERO)
            .currency(wallet.getCurrency())
            .balanceBefore(before)
            .balanceAfter(wallet.getBalance())
            .description(request.getDescription())
            .channel(request.getChannel())
            .build();

        Transaction saved = transactionRepository.save(tx);
        log.info("Wallet debited: {} ETB from {} | ref: {}", request.getAmount(), wallet.getWalletNumber(), ref);
        return toTxResponse(saved);
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        log.info("Transfer: {} from {} to {}", request.getAmount(), request.getFromWalletId(), request.getToWalletId());

        if (request.getFromWalletId().equals(request.getToWalletId()))
            throw new BusinessException("Cannot transfer to the same wallet");

        Wallet from = walletRepository.findById(request.getFromWalletId())
            .orElseThrow(() -> new NotFoundException("Source wallet not found"));
        Wallet to = walletRepository.findById(request.getToWalletId())
            .orElseThrow(() -> new NotFoundException("Destination wallet not found"));

        if (!from.isActive()) throw new BusinessException("Source wallet is not active");
        if (!to.isActive()) throw new BusinessException("Destination wallet is not active");
        if (!from.hasEnoughBalance(request.getAmount()))
            throw new BusinessException("Insufficient balance in source wallet");

        String ref = generateReference("TRF");

        DebitWalletRequest debitReq = DebitWalletRequest.builder()
            .walletId(from.getId()).amount(request.getAmount())
            .transactionType(TransactionType.TRANSFER)
            .description("Transfer to " + to.getWalletNumber())
            .channel(request.getChannel()).build();

        CreditWalletRequest creditReq = CreditWalletRequest.builder()
            .walletId(to.getId()).amount(request.getAmount())
            .transactionType(TransactionType.TRANSFER)
            .description("Transfer from " + from.getWalletNumber())
            .channel(request.getChannel()).build();

        TransactionResponse debitTx = debit(debitReq);
        TransactionResponse creditTx = credit(creditReq);

        return TransferResponse.builder()
            .reference(ref)
            .fromWalletId(from.getId())
            .toWalletId(to.getId())
            .amount(request.getAmount())
            .currency(from.getCurrency())
            .debitTransactionId(debitTx.getId())
            .creditTransactionId(creditTx.getId())
            .status("COMPLETED")
            .build();
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactions(String walletId, Pageable pageable) {
        return transactionRepository.findByWalletId(walletId, pageable).map(this::toTxResponse);
    }

    @Transactional
    public void freezeWallet(String walletId, String reason) {
        walletRepository.findById(walletId).orElseThrow(() -> new NotFoundException("Wallet not found"));
        walletRepository.updateStatus(walletId, WalletStatus.FROZEN);
        log.info("Wallet frozen: {} reason: {}", walletId, reason);
    }

    @Transactional
    public void unfreezeWallet(String walletId) {
        walletRepository.findById(walletId).orElseThrow(() -> new NotFoundException("Wallet not found"));
        walletRepository.updateStatus(walletId, WalletStatus.ACTIVE);
        log.info("Wallet unfrozen: {}", walletId);
    }

    // Helpers
    private String generateWalletNumber() {
        return "1000" + String.format("%09d", new Random().nextInt(1000000000));
    }

    private String generateReference(String prefix) {
        return prefix + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private WalletResponse toResponse(Wallet w) {
        return WalletResponse.builder()
            .id(w.getId()).userId(w.getUserId()).walletNumber(w.getWalletNumber())
            .balance(w.getBalance()).availableBalance(w.getAvailableBalance())
            .reservedBalance(w.getReservedBalance()).currency(w.getCurrency())
            .status(w.getStatus().toString()).walletType(w.getWalletType().toString())
            .dailyLimit(w.getDailyLimit()).monthlyLimit(w.getMonthlyLimit())
            .singleTxLimit(w.getSingleTxLimit()).dailySpent(w.getDailySpent())
            .monthlySpent(w.getMonthlySpent()).totalCredited(w.getTotalCredited())
            .totalDebited(w.getTotalDebited()).lastTransactionAt(w.getLastTransactionAt())
            .createdAt(w.getCreatedAt()).build();
    }

    private TransactionResponse toTxResponse(Transaction t) {
        return TransactionResponse.builder()
            .id(t.getId()).reference(t.getReference()).walletId(t.getWalletId())
            .userId(t.getUserId()).type(t.getType().toString()).direction(t.getDirection().toString())
            .status(t.getStatus().toString()).amount(t.getAmount()).fee(t.getFee())
            .currency(t.getCurrency()).balanceBefore(t.getBalanceBefore())
            .balanceAfter(t.getBalanceAfter()).description(t.getDescription())
            .channel(t.getChannel()).createdAt(t.getCreatedAt()).build();
    }

    record WalletCreditedMsg(String userId, String walletId, String amount, String reference) {}
}