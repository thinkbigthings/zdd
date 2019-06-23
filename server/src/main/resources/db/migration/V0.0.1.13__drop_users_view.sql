
set search_path TO public;

DROP VIEW users;

ALTER INDEX index_user_registration_time RENAME TO index_app_user_registration_time;

ALTER INDEX index_user_username RENAME TO index_app_user_username;
