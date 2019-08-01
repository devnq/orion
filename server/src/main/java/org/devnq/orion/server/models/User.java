package org.devnq.orion.server.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.devnq.orion.server.models.ImmutableUser;
import org.immutables.value.Value.Immutable;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.util.UUID;

@Immutable
@JsonSerialize(as = ImmutableUser.class)
@JsonDeserialize(as = ImmutableUser.class)
public abstract class User {
    @Nullable
    public abstract UUID id();

    public abstract String username();

    public abstract String name();

    @Nullable
    public abstract String password();

    public static User valueOf(final JSONObject object) {
        final String username = object.getString("username");
        final String name = object.getString("name");
        final String password = object.getString("password");
        return ImmutableUser.builder()
            .username(username)
            .name(name)
            .password(password)
            .build();
    }
}
