package com.microfinance.wallet.dto;
import com.microfinance.wallet.domain.model.TransactionType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DebitWalletRequest {
    @NotBlank private String walletId;
    @NotNull @Positive private BigDecimal amount;
    private TransactionType transactionType;
    private String description;
    private String channel;
}