package org.devnq.orion.server.dal;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import ninja.jdbi.NinjaJdbi;
import ninja.jpa.UnitOfWork;
import org.devnq.orion.server.models.Event;
import org.devnq.orion.server.models.ImmutableEvent;
import org.devnq.orion.server.models.User;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static java.util.UUID.fromString;
import static org.devnq.orion.server.dal.UserDalUtils.findUser;

public class EventDal {
    private final DBI jdbi;
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String HOST = "host";
    private static final String EPOCH = "epoch";

    @Inject
    public EventDal(final NinjaJdbi jdbi) {
        this.jdbi = jdbi.getDbi("default");
    }

    @Transactional
    public Event insert(final User host, final Event newEvent) {
        try (final Handle conn = jdbi.open()) {
            final Map<String, Object> first = conn.createStatement(
                "INSERT INTO events (host, title, description, epoch) VALUES (:host,:title,:description,:epoch);")
                .bind("host", host.id())
                .bind("title", newEvent.title())
                .bind("description", newEvent.description())
                .bind("epoch", newEvent.epoch())
                .executeAndReturnGeneratedKeys()
                .first();
            final UUID eventId = (UUID) first.get("scope_identity()");
            return find(conn, eventId);
        }
    }

    @UnitOfWork
    public Event find(final UUID eventId) {
        try (final Handle conn = jdbi.open()) {
            return find(conn, eventId);
        }
    }

    @UnitOfWork
    public Collection<Event> future() {
        try (final Handle conn = jdbi.open()) {
            return conn
                .createQuery(
                    "SELECT * FROM events WHERE epoch >= (SELECT DATEDIFF('second',timestamp '1970-01-01 00:00:00', CURRENT_TIMESTAMP())) ORDER BY epoch ASC;")
                .map(new EventMapper(conn))
                .list();
        }
    }

    private static Event find(final Handle conn, final UUID eventId) {
        return conn
            .createQuery("SELECT * FROM events WHERE id = :id;")
            .bind("id", eventId)
            .map(new EventMapper(conn))
            .first();
    }

    static class EventMapper implements ResultSetMapper<Event> {
        private final Handle conn;

        EventMapper(final Handle conn) {
            this.conn = conn;
        }

        @Override
        public Event map(final int index,
                         final ResultSet r,
                         final StatementContext ctx) throws SQLException {
            final String hostIdStr = r.getString(HOST);
            final UUID eventId = fromString(r.getString(ID));
            final String title = r.getString(TITLE);
            final int epoch = r.getInt(EPOCH);
            final String description = r.getString(DESCRIPTION);
            //FIXME. There are better ways. This will perform multiple unnecessary DB requests.
            final User user = findUser(conn, fromString(hostIdStr));
            return ImmutableEvent.builder()
                .id(eventId)
                .title(title)
                .description(description)
                .epoch(epoch)
                .host(user)
                .build();
        }
    }
}
