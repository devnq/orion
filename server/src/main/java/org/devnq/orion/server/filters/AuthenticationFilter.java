package org.devnq.orion.server.filters;

import com.google.inject.Inject;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import org.devnq.orion.server.crypt.Tokens;
import org.devnq.orion.server.dal.UserDal;
import org.devnq.orion.server.etc.Authenticated;
import org.devnq.orion.server.etc.UnauthorizedException;
import org.devnq.orion.server.models.User;

import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class AuthenticationFilter implements Filter {

    public static final String AUTH_USER_ATTRIBUTE = "AUTH_USER";
    private final Tokens tokens;
    private final UserDal userDal;

    @Inject
    public AuthenticationFilter(final Tokens tokens, final UserDal userDal) {
        this.tokens = tokens;
        this.userDal = userDal;
    }

    @Override
    public Result filter(final FilterChain filterChain, final Context context) {
        final Authenticated annotation = context.getRoute().getControllerMethod().getAnnotation(Authenticated.class);
        if (nonNull(annotation)) {
            final String authorization = context.getHeader("authorization");
            if (isEmpty(authorization) || !authorization.startsWith("bearer ")) {
                throw new UnauthorizedException("Authorization Bearer Missing");
            }
            final String token = authorization.substring("bearer ".length());
            final UUID userId = tokens.decode(token);
            final User loggedInUser = userDal.find(userId);
            if (isNull(loggedInUser)) {
                throw new UnauthorizedException("Unknown User for Token");
            } else {
                context.setAttribute(AUTH_USER_ATTRIBUTE, loggedInUser);
            }
        }
        return filterChain.next(context);
    }
}
