package com.microfinance.event.loan;

import com.microfinance.event.BaseEvent;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @EqualsAndHashCode(callSuper = true)
public class LoanApprovedEvent extends BaseEvent {
    private String loanId;
    private String borrowerId;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private int tenureMonths;
    private String loanType;

    public LoanApprovedEvent(String loanId, String borrowerId, BigDecimal amount,
                              BigDecimal interestRate, int tenureMonths) {
        super("LOAN_APPROVED", "loan-service");
        this.loanId = loanId; this.borrowerId = borrowerId;
        this.amount = amount; this.interestRate = interestRate; this.tenureMonths = tenureMonths;
    }
}
