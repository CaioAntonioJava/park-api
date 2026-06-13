package com.caiohenrique.demo_park_api.exception;

public class ActiveParkingSessionAlreadyExistsException extends RuntimeException {
    public ActiveParkingSessionAlreadyExistsException(String message) {
        super(message);
    }
}
