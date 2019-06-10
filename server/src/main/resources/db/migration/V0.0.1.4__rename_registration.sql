
set search_path TO public;


-- start with nullable column and no default, so we can fill it
ALTER TABLE users ADD COLUMN registration_time TIMESTAMPTZ;

-- default value is not constant,
-- so need to check for NULL to see if old value wasn't written by new code
-- remove default on old column, so trigger fills it instead
ALTER TABLE users ALTER COLUMN registration DROP DEFAULT;

-- as old software is writing null values from not knowing about the new column
-- fill in the null data with a default value
CREATE OR REPLACE FUNCTION set_registration() RETURNS TRIGGER AS
$BODY$
BEGIN

   -- new column can set the value into the old column
   IF NEW.registration IS NULL THEN
       NEW.registration = NEW.registration_time;

   -- old column can set the value into the new column
   ELSIF NEW.registration_time IS NULL THEN
       NEW.registration_time = NEW.registration;

   END IF;

   RETURN NEW;

END;
$BODY$
LANGUAGE plpgsql;

-- rename can use same function here because registration value is insert only
CREATE TRIGGER set_registration_on_update
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION set_registration();

CREATE TRIGGER set_registration_on_insert
    BEFORE INSERT ON users
    FOR EACH ROW
    EXECUTE FUNCTION set_registration();

-- there should be no new null values, so can populate existing null values now
UPDATE users SET registration_time = registration WHERE registration_time IS NULL;

-- and finally can add not null constraint and default value
ALTER TABLE users ALTER COLUMN registration_time SET NOT NULL;
