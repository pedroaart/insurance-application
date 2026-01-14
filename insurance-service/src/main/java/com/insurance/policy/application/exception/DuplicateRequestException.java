package com.insurance.policy.application.exception;

public class DuplicateRequestException extends RuntimeException {
    private final String idempotencyKey;
    
    public DuplicateRequestException(String message, String idempotencyKey) {
        super(message);
        this.idempotencyKey = idempotencyKey;
    }
    
    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
