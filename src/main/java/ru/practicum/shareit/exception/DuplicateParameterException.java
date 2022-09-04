package ru.practicum.shareit.exception;

public class DuplicateParameterException extends RuntimeException {
    public DuplicateParameterException(String message) {
        super(message);
    }
}
