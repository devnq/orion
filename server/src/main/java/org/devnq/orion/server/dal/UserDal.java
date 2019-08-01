package org.devnq.orion.server.dal;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import ninja.jdbi.NinjaJdbi;
import ninja.jpa.UnitOfWork;
import org.devnq.orion.server.crypt.BCrypt;
import org.devnq.orion.server.models.User;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.devnq.orion.server.dal.UserDalUtils.HASH;
import static org.devnq.orion.server.dal.UserDalUtils.ID;
import static org.devnq.orion.server.dal.UserDalUtils.USERNAME;
import static org.devnq.orion.server.dal.UserDalUtils.findUser;

public class UserDal {
    private final DBI jdbi;

    @Inject
    public UserDal(final NinjaJdbi jdbi) {
        this.jdbi = jdbi.getDbi("default");
    }

    @Transactional
    public User insert(final User newUser) {
        try (final Handle conn = jdbi.open()) {
            final String password = requireNonNull(newUser.password());
            final String hash = BCrypt.hash(password);
            conn.insert("INSERT INTO users (username, name, hash) VALUES (?,?,?);",
                        newUser.username(),
                        newUser.name(),
                        hash);
            final String username = newUser.username();
            return findUser(conn, username);
        }
    }

    @UnitOfWork
    @Nullable
    public User find(final UUID id) {
        try (final Handle conn = jdbi.open()) {
            return findUser(conn, id);
        }
    }

    @UnitOfWork
    @Nullable
    public UUID findId(final String username) {
        final Map<String, Object> results;
        try (final Handle conn = jdbi.open()) {
            results = conn
                .createQuery("SELECT id FROM users WHERE lower(username) = lower(:username);")
                .bind(USERNAME, username)
                .first();
        }
        return nonNull(results) ? (UUID) results.get(ID) : null;
    }

    @UnitOfWork
    public boolean validatePassword(final String username, final String password) {
        final Map<String, Object> userHash;
        try (final Handle conn = jdbi.open()) {
            userHash = conn
                .createQuery("SELECT hash FROM users WHERE lower(username) = lower(:username) LIMIT 1;")
                .bind(USERNAME, username)
                .first();
        }
        if (isNull(userHash)) {
            return false;
        } else {
            final String hash = (String) userHash.get(HASH);
            return BCrypt.verify(hash, password);
        }
    }

    @UnitOfWork
    public boolean isUsernameAvailable(final String username) {
        final Object o;
        try (final Handle conn = jdbi.open()) {
            o = conn
                .createQuery("SELECT 1=1 as \"exists\" FROM users WHERE lower(username) = lower(:username);")
                .bind(USERNAME, username)
                .first();
        }
        return isNull(o);
    }
}
