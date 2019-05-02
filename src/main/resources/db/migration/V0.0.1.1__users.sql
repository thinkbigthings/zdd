
set search_path = public, extensions;

CREATE TABLE users (
    id           BIGSERIAL    NOT NULL PRIMARY KEY,
    external_id  UUID         NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    username     VARCHAR      NOT NULL UNIQUE,
    email        VARCHAR      NOT NULL DEFAULT '',
    display_name VARCHAR      NOT NULL DEFAULT '',
    enabled      BOOLEAN      NOT NULL DEFAULT FALSE,
    registration TIMESTAMPTZ
);