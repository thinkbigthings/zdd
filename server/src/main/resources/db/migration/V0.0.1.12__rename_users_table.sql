
set search_path TO public;

ALTER TABLE users RENAME TO app_user;

CREATE VIEW users AS (SELECT * FROM app_user) WITH CASCADED CHECK OPTION;
