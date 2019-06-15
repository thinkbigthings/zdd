
-- this is a non-transactional statement, best to not mix transactional statements in your migration

CREATE INDEX CONCURRENTLY index_user_registration_time ON users(registration_time);

CREATE INDEX CONCURRENTLY index_user_username ON users(username);
