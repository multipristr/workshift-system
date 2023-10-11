package org.exception;

public class LogicValidationException extends RuntimeException {
    private static final long serialVersionUID = 4149132481115595075L;

    public LogicValidationException(String message) {
        super(message);
    }
}
