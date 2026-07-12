package com.microfinance.event.wallet;

import com.microfinance.event.BaseEvent;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @EqualsAndHashCode(callSuper = true)
public class WalletCreditedEvent extends BaseEvent {
    private String walletId;
    private String userId;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String currency;
    private String reference;
    private String description;

    public WalletCreditedEvent(String walletId, String userId, BigDecimal amount,
                                BigDecimal balanceAfter, String currency, String reference) {
        super("WALLET_CREDITED", "wallet-service");
        this.walletId = walletId; this.userId = userId;
        this.amount = amount; this.balanceAfter = balanceAfter;
        this.currency = currency; this.reference = reference;
    }
}
