package org.devnq.orion.server.etc;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(final String message) {
        super(message);
    }

    public UnauthorizedException(final String message,
                                 final Throwable cause,
                                 final boolean enableSuppression,
                                 final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
