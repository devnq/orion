package org.devnq.orion.server.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.util.UUID;

@Immutable
@JsonSerialize(as = ImmutableEvent.class)
@JsonDeserialize(as = ImmutableEvent.class)
public abstract class Event {

    @Nullable
    public abstract UUID id();

    @Nullable
    public abstract User host();

    public abstract String title();

    public abstract String description();

    public abstract long epoch();

    public static Event valueOf(final JSONObject newEvent) {
        return ImmutableEvent.builder()
            .title(newEvent.getString("title"))
            .description(newEvent.getString("description"))
            .epoch(newEvent.getLong("epoch"))
            .build();
    }
}
