package org.exception;

public class LogicalValidationException extends RuntimeException {
    private static final long serialVersionUID = 4149132481115595075L;

    public LogicalValidationException(String message) {
        super(message);
    }
}
