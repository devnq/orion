package org.devnq.orion.server.dal;

import org.devnq.orion.server.models.ImmutableUser;
import org.devnq.orion.server.models.User;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

final class UserDalUtils {
    static final String HASH = "hash";
    static final String ID = "id";
    static final String USERNAME = "username";
    static final String NAME = "name";
    static final UserMapper USER_MAPPER = new UserMapper();

    private UserDalUtils() {}

    private static class UserMapper implements ResultSetMapper<User> {
        @Override
        public User map(final int index,
                        final ResultSet r,
                        final StatementContext ctx) throws SQLException {
            return ImmutableUser.builder()
                .id(UUID.fromString(r.getString(ID)))
                .name(r.getString("name"))
                .username(r.getString("username"))
                .build();
        }
    }

    static User findUser(final Handle conn, final String username) {
        return conn
            .createQuery("SELECT id, username, name FROM users WHERE lower(username) = lower(:username);")
            .bind(USERNAME, username)
            .map(new UserMapper())
            .first();
    }

    static User findUser(final Handle conn, final UUID id) {
        return conn
            .createQuery("SELECT id, username, name FROM users WHERE id = :id;")
            .bind(ID, id)
            .map(new UserMapper())
            .first();
    }
}
