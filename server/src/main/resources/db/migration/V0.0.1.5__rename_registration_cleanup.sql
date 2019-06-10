
set search_path TO public;


-- triggers aren't being used any more, drop those first
DROP TRIGGER IF EXISTS set_registration_on_update ON users;
DROP TRIGGER IF EXISTS set_registration_on_insert ON users;

-- can set default on new column, not populated by old trigger any more if not set
ALTER TABLE users ALTER COLUMN registration_time SET DEFAULT now();

-- then the functions which aren't used any more
DROP FUNCTION IF EXISTS set_registration;

-- finally the column which isn't used any more
ALTER TABLE users DROP COLUMN registration;
