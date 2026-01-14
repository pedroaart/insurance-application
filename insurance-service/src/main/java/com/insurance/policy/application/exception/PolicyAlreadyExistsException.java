package com.insurance.policy.application.exception;

public class PolicyAlreadyExistsException extends RuntimeException {
    public PolicyAlreadyExistsException(String message) {
        super(message);
    }
}
