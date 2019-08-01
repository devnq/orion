package org.devnq.orion.server.api;

import com.google.inject.Inject;
import ninja.Result;
import ninja.Results;
import org.devnq.orion.server.crypt.Tokens;
import org.devnq.orion.server.dal.UserDal;
import org.devnq.orion.server.etc.AuthUser;
import org.devnq.orion.server.etc.Authenticated;
import org.devnq.orion.server.etc.JsonBodySchema;
import org.devnq.orion.server.etc.JsonSchema;
import org.devnq.orion.server.models.User;
import org.json.JSONObject;

import java.util.UUID;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Objects.requireNonNull;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.eclipse.jetty.http.HttpStatus.UNAUTHORIZED_401;

public class Auth {
    private static final String LOGIN_SCHEMA = "Login";

    @Inject
    UserDal userDal;

    @Inject
    Tokens tokens;

    @Authenticated
    public Result get(@AuthUser final User user) {
        return Results
            .status(OK_200)
            .render(user);
    }

    @JsonBodySchema(schema = LOGIN_SCHEMA)
    public Result post(@JsonSchema final JSONObject credentials) {
        final String username = credentials.getString("username");
        final String password = credentials.getString("password");
        final boolean validPassword = userDal.validatePassword(username, password);
        if (validPassword) {
            final UUID userId = requireNonNull(userDal.findId(username));
            final String token = tokens.encode(userId);
            return Results
                .status(OK_200)
                .render(of("token", token));
        } else {
            return Results.status(UNAUTHORIZED_401);
        }
    }
}
