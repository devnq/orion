package org.devnq.orion.server.api;

import com.jcabi.http.Response;
import org.devnq.orion.server.ApiTest;
import org.devnq.orion.server.models.Event;
import org.devnq.orion.server.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static com.jcabi.http.Request.GET;
import static com.jcabi.http.Request.POST;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.devnq.orion.server.Generators.genEvent;
import static org.devnq.orion.server.Generators.genNewUser;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("/api/events")
class EventsApiTest extends ApiTest {
    protected static final String EVENTS_URL = "/api/events";

    private String token;
    private Long now;

    @BeforeEach
    void setUp() throws IOException {
        final User host = genNewUser().build();
        postNewUser(host);
        token = authToken(host);
        now = Instant.now().getEpochSecond();
    }

    @Test
    @DisplayName("GET returns all future events")
    void getEvents() throws IOException {
        //Arrange
        final Event pastEvent = genEvent().epoch(now - 1000).build();
        final Event futureEvent1 = genEvent().epoch(now + 1000).build();
        final Event futureEvent2 = genEvent().epoch(now + 2000).build();
        final Event actualPastEvent = fromPostEvent(token, pastEvent);
        final Event actualFutureEvent1 = fromPostEvent(token, futureEvent1);
        final Event actualFutureEvent2 = fromPostEvent(token, futureEvent2);

        //Act
        final Response response = getFutureEvents();

        //Assert
        assertThat(response.status()).isEqualTo(OK_200);
        final List<Event> actualEvents = asList(mapper.readValue(response.body(), Event[].class));
        assertThat(actualEvents).contains(actualFutureEvent1, actualFutureEvent2);
        assertThat(actualEvents).doesNotContain(actualPastEvent);
    }

    @Test
    @DisplayName("POST new event should require an authenticated user")
    void postEvent() {
        fail("FIXME");
    }

    private Event fromPostEvent(final String token, final Event newEvent) throws IOException {
        final Response response = postEvent(token, newEvent);
        assertThat(response.status()).isEqualTo(CREATED_201);
        return mapper.readValue(response.body(), Event.class);
    }

    protected Response postEvent(final String authToken, @Nullable final Event event) throws IOException {
        final String json = mapper.writeValueAsString(event);
        return requestTo(EVENTS_URL)
            .method(POST)
            .body()
            .set(json)
            .back()
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .header(AUTHORIZATION, "bearer " + authToken)
            .fetch();
    }

    protected Response getFutureEvents() throws IOException {
        return requestTo(EVENTS_URL)
            .method(GET)
            .header(CONTENT_TYPE, APPLICATION_JSON)
            .fetch();
    }
}
