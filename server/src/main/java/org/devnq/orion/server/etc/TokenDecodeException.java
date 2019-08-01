package org.devnq.orion.server.etc;

public class TokenDecodeException extends UnauthorizedException {
    public TokenDecodeException(final String message) {
        super(message);
    }
}
