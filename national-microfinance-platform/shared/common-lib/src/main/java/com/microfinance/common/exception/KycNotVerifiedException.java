package com.microfinance.common.exception;
public class KycNotVerifiedException extends RuntimeException {
    public KycNotVerifiedException(String userId) { super("KYC required for user: " + userId); }
}
