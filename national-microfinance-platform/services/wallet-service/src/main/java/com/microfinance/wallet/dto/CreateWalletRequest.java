package com.microfinance.wallet.dto;
import com.microfinance.wallet.domain.model.WalletType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateWalletRequest {
    @NotBlank private String userId;
    private WalletType walletType;
}