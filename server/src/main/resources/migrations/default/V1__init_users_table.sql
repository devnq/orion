CREATE SCHEMA IF NOT EXISTS "public";

CREATE TABLE users (
    id       UUID DEFAULT random_uuid(),
    username VARCHAR_IGNORECASE(255),
    name     VARCHAR(255),
    hash     VARCHAR(1024),
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX users_username_unique ON users(username);
