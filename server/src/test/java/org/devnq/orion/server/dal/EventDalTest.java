package org.devnq.orion.server.dal;

import ninja.jdbi.NinjaJdbi;
import org.devnq.orion.server.DalTest;
import org.devnq.orion.server.models.Event;
import org.devnq.orion.server.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.devnq.orion.server.Generators.genEvent;
import static org.devnq.orion.server.Generators.genNewUser;

class EventDalTest extends DalTest {

    private final EventDal eventDal;
    private final User host;

    EventDalTest() {
        init();
        final NinjaJdbi instance = getInstance(NinjaJdbi.class);
        final UserDal userDal = new UserDal(instance);
        eventDal = new EventDal(instance);
        host = userDal.insert(genNewUser().build());
    }

    @Test
    @DisplayName("Inserts and returns a new event")
    void insert() {
        //Arrange
        final Event event = genEvent().build();

        //Act
        final Event actual = eventDal.insert(host, event);

        //Assert
        assertThat(actual).isEqualToIgnoringGivenFields(event, "host", "id");
        assertThat(actual.id()).isInstanceOf(UUID.class);
        assertThat(actual.host()).isEqualTo(host);
    }

    @Test
    @DisplayName("Finds an event by id")
    void find() {
        //Arrange
        final Event newEvent = genEvent().build();
        final Event insertedEvent = eventDal.insert(host, newEvent);

        //Act
        final Event actual = eventDal.find(insertedEvent.id());

        //Assert
        assertThat(actual).isEqualTo(insertedEvent);
    }

    @Test
    @DisplayName("Lists all events in the future. Note test should be set to run as UTC (JVM: -Duser.timezone=UTC)")
    void future() {
        //Arrange
        final Long now = Instant.now().getEpochSecond();
        final Event pastEvent = genEvent().epoch(now - 100).build();
        final Event futureEvent1 = genEvent().epoch(now + 100).build();
        final Event futureEvent2 = genEvent().epoch(now + 300).build();
        final Event futureEvent3 = genEvent().epoch(now + 500).build();
        final List<Event> insertedEvents = Stream.of(pastEvent, futureEvent1, futureEvent2, futureEvent3)
            .map(event -> eventDal.insert(host, event))
            .collect(Collectors.toList());

        //Act
        final Collection<Event> actual = eventDal.future();

        //Assert
        assertThat(actual).containsAll(insertedEvents.subList(1, 4));
    }
}