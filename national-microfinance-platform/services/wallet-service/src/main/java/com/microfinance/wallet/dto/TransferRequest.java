package com.microfinance.wallet.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TransferRequest {
    @NotBlank private String fromWalletId;
    @NotBlank private String toWalletId;
    @NotNull @Positive private BigDecimal amount;
    private String description;
    private String channel;
}