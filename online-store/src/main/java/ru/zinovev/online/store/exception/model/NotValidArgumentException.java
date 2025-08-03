package ru.zinovev.online.store.exception.model;

public class NotValidArgumentException extends RuntimeException {
    public NotValidArgumentException(String message) {
        super(message);
    }
}
