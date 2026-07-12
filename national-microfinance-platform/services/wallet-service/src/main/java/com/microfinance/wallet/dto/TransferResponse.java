package com.microfinance.wallet.dto;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransferResponse {
    private String reference;
    private String fromWalletId;
    private String toWalletId;
    private BigDecimal amount;
    private String currency;
    private String debitTransactionId;
    private String creditTransactionId;
    private String status;
}