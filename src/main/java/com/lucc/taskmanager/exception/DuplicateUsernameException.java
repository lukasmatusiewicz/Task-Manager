package com.lucc.taskmanager.exception;

public class DuplicateUsernameException extends IllegalArgumentException {
    public DuplicateUsernameException(String message) {
        super(message);
    }
}
