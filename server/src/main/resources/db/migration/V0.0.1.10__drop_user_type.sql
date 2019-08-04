
set search_path TO public;

ALTER TABLE users DROP COLUMN type_name;

DROP TABLE user_type;
