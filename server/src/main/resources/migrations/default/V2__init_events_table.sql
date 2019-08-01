CREATE TABLE events (
    id          UUID DEFAULT random_uuid(),
    host        UUID NOT NULL,
    title       VARCHAR(255),
    description CLOB,
    epoch       BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (host) REFERENCES users(id)
);
