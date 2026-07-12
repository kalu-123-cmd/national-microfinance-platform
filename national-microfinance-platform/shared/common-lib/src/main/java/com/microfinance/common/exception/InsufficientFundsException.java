package com.microfinance.common.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(BigDecimal available, BigDecimal required) {
        super(String.format("Insufficient funds. Available: %.2f, Required: %.2f", available, required));
    }
    public InsufficientFundsException(String msg) { super(msg); }
}
