package org.devnq.orion.server.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.json.JSONObject;

import static java.util.Objects.requireNonNull;

@Immutable
@JsonSerialize(as = ImmutableLogin.class)
@JsonDeserialize(as = ImmutableLogin.class)
public abstract class Login {

    public abstract String username();

    public abstract String password();

    public static Login valueOf(final JSONObject object) {
        final String username = object.getString("username");
        final String password = object.getString("password");
        return ImmutableLogin.builder()
            .username(username)
            .password(password)
            .build();
    }

    public static Login valueOf(final User user) {
        final String password = requireNonNull(user.password());
        return ImmutableLogin.builder()
            .username(user.username())
            .password(password)
            .build();
    }
}
