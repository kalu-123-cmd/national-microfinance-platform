package com.microfinance.event.payment;

import com.microfinance.event.BaseEvent;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @EqualsAndHashCode(callSuper = true)
public class PaymentCompletedEvent extends BaseEvent {
    private String paymentId;
    private String senderId;
    private String receiverId;
    private BigDecimal amount;
    private BigDecimal fee;
    private String currency;
    private String reference;

    public PaymentCompletedEvent(String paymentId, String senderId, String receiverId,
                                  BigDecimal amount, String currency) {
        super("PAYMENT_COMPLETED", "payment-service");
        this.paymentId = paymentId; this.senderId = senderId;
        this.receiverId = receiverId; this.amount = amount; this.currency = currency;
    }
}
