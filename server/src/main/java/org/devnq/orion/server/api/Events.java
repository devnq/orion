package org.devnq.orion.server.api;

import com.google.inject.Inject;
import ninja.Result;
import ninja.Results;
import org.devnq.orion.server.dal.EventDal;
import org.devnq.orion.server.etc.AuthUser;
import org.devnq.orion.server.etc.Authenticated;
import org.devnq.orion.server.etc.JsonBodySchema;
import org.devnq.orion.server.etc.JsonSchema;
import org.devnq.orion.server.models.Event;
import org.devnq.orion.server.models.User;
import org.json.JSONObject;

import java.util.Collection;

import static org.eclipse.jetty.http.HttpStatus.CREATED_201;
import static org.eclipse.jetty.http.HttpStatus.OK_200;

public class Events {
    private static final String NEW_EVENT_SCHEMA = "NewEvent";

    @Inject
    EventDal eventDal;

    @Authenticated
    @JsonBodySchema(schema = NEW_EVENT_SCHEMA)
    public Result post(@AuthUser final User authUser,
                       @JsonSchema final JSONObject newEvent) {
        final Event event = Event.valueOf(newEvent);
        final Event insertedEvent = eventDal.insert(authUser, event);
        return Results
            .status(CREATED_201)
            .render(insertedEvent);
    }

    public Result get() {
        final Collection<Event> events = eventDal.future();
        return Results
            .status(OK_200)
            .render(events);
    }
}
