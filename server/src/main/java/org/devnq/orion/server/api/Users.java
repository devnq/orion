package org.devnq.orion.server.api;

import com.google.inject.Inject;
import ninja.Result;
import ninja.Results;
import org.devnq.orion.server.dal.UserDal;
import org.devnq.orion.server.etc.JsonBodySchema;
import org.devnq.orion.server.etc.JsonSchema;
import org.devnq.orion.server.models.User;
import org.json.JSONObject;

import static org.eclipse.jetty.http.HttpStatus.CONFLICT_409;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;

public class Users {
    private static final String NEW_USER_SCHEMA = "NewUser";

    @Inject
    UserDal userDal;

    @JsonBodySchema(schema = NEW_USER_SCHEMA)
    public Result post(@JsonSchema final JSONObject newUser) {
        final User user = User.valueOf(newUser);
        final String username = user.username();
        final boolean usernameIsAvailable = userDal.isUsernameAvailable(username);
        if (usernameIsAvailable) {
            final User insertedUser = userDal.insert(user);
            return Results
                .status(CREATED_201)
                .render(insertedUser);
        } else {
            return Results.status(CONFLICT_409);
        }
    }
}
