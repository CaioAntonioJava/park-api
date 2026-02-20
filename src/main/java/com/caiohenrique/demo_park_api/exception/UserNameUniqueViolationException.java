package com.caiohenrique.demo_park_api.exception;

public class UserNameUniqueViolationException extends RuntimeException {
    public UserNameUniqueViolationException(String message) {
        super(message);
    }
}