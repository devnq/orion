package org.devnq.orion.server.crypt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.inject.Inject;
import ninja.utils.NinjaProperties;
import org.devnq.orion.server.etc.TokenDecodeException;

import java.util.UUID;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static java.util.Objects.nonNull;
import static java.util.UUID.fromString;

public class Tokens {
    private static final String USER_ID_CLAIM = "userId";

    private final String secret;

    @Inject
    public Tokens(final NinjaProperties ninjaProperties) {
        secret = ninjaProperties.get("application.secret");
    }

    public Tokens(final String secret) {
        this.secret = secret;
    }

    public String encode(final UUID userId) {
        final Algorithm algorithm = HMAC256(secret);
        return JWT.create()
            .withClaim(USER_ID_CLAIM, userId.toString())
            .sign(algorithm);
    }

    public UUID decode(final String token) {
        final DecodedJWT decode = JWT.decode(token);
        final Claim claim = decode.getClaim(USER_ID_CLAIM);
        if (nonNull(claim)) {
            final String id = claim.asString();
            return fromString(id);
        } else {
            throw new TokenDecodeException("Unable to extract userId from token");
        }
    }
}
