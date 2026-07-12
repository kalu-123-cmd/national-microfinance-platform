package com.microfinance.savings.dto;

import com.microfinance.savings.domain.model.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class SavingsAccountResponse {
    private String id;
    private String userId;
    private String accountNumber;
    private String accountName;
    private AccountType accountType;
    private BigDecimal balance;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    private BigDecimal annualInterestRatePercent; // interestRate * 100
    private CompoundingFrequency compoundingFreq;
    private String currency;
    private AccountStatus status;
    private LocalDate lastInterestDate;
    private Instant lastTransactionAt;
    private Instant createdAt;

    public static SavingsAccountResponse from(SavingsAccount a) {
        return SavingsAccountResponse.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .accountNumber(a.getAccountNumber())
                .accountName(a.getAccountName())
                .accountType(a.getAccountType())
                .balance(a.getBalance())
                .minimumBalance(a.getMinimumBalance())
                .interestRate(a.getInterestRate())
                .annualInterestRatePercent(a.getInterestRate().multiply(BigDecimal.valueOf(100)))
                .compoundingFreq(a.getCompoundingFreq())
                .currency(a.getCurrency())
                .status(a.getStatus())
                .lastInterestDate(a.getLastInterestDate())
                .lastTransactionAt(a.getLastTransactionAt())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
