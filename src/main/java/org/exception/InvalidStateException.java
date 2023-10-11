package org.exception;

public class InvalidStateException extends RuntimeException {
    private static final long serialVersionUID = -6184877281192891866L;

    public InvalidStateException(String message) {
        super(message);
    }
}
