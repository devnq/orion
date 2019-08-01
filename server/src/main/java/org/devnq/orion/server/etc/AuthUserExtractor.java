package org.devnq.orion.server.etc;

import ninja.Context;
import ninja.params.ArgumentExtractor;
import org.devnq.orion.server.filters.AuthenticationFilter;
import org.devnq.orion.server.models.User;

import static java.util.Objects.isNull;

public class AuthUserExtractor implements ArgumentExtractor<User> {

    @Override
    public User extract(final Context context) {
        final User user = (User) context.getAttribute(AuthenticationFilter.AUTH_USER_ATTRIBUTE);
        if (isNull(user)) {
            throw new UnauthorizedException("Auth User attribute missing");
        } else {
            return user;
        }
    }

    @Override
    public Class<User> getExtractedType() {
        return User.class;
    }

    @Override
    public String getFieldName() {
        return null;
    }
}
