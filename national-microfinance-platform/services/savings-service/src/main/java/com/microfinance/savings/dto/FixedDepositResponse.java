package com.microfinance.savings.dto;

import com.microfinance.savings.domain.model.FixedDeposit;
import com.microfinance.savings.domain.model.FixedDepositStatus;
import com.microfinance.savings.domain.model.CompoundingFrequency;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class FixedDepositResponse {
    private String id;
    private String userId;
    private String accountId;
    private String depositNumber;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private BigDecimal annualInterestRatePercent;
    private Integer tenureMonths;
    private CompoundingFrequency compoundingFreq;
    private BigDecimal maturityAmount;
    private LocalDate maturityDate;
    private FixedDepositStatus status;
    private boolean autoRenew;
    private BigDecimal penaltyRate;
    private BigDecimal interestEarned;
    private Instant createdAt;

    public static FixedDepositResponse from(FixedDeposit fd) {
        return FixedDepositResponse.builder()
                .id(fd.getId())
                .userId(fd.getUserId())
                .accountId(fd.getAccountId())
                .depositNumber(fd.getDepositNumber())
                .principalAmount(fd.getPrincipalAmount())
                .interestRate(fd.getInterestRate())
                .annualInterestRatePercent(fd.getInterestRate().multiply(BigDecimal.valueOf(100)))
                .tenureMonths(fd.getTenureMonths())
                .compoundingFreq(fd.getCompoundingFreq())
                .maturityAmount(fd.getMaturityAmount())
                .maturityDate(fd.getMaturityDate())
                .status(fd.getStatus())
                .autoRenew(fd.isAutoRenew())
                .penaltyRate(fd.getPenaltyRate())
                .interestEarned(fd.getInterestEarned())
                .createdAt(fd.getCreatedAt())
                .build();
    }
}
