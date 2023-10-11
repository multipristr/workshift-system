package org.exception;

public class MissingEntityException extends RuntimeException {
    private static final long serialVersionUID = -6184877281192891866L;

    public MissingEntityException(String message) {
        super(message);
    }
}
