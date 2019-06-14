
set search_path TO public;


-- add the new column for the new type
ALTER TABLE users ADD COLUMN height_cm INTEGER NOT NULL DEFAULT 0;

-- original default was an empty string, turn it into something we can parse
ALTER TABLE users ALTER COLUMN height SET DEFAULT '0';
UPDATE users SET height='0' WHERE height='';

-- populate new column with translated data from old column
UPDATE users SET height_cm = cast(height as INTEGER);


-- as old software is writing missing values in one column
-- fill in the missing data with the translated value from the other column
-- use as much information as we have for each conditional statement:
-- see if values are different and if only one side has default value

CREATE OR REPLACE FUNCTION sync_height_on_insert() RETURNS TRIGGER AS
$BODY$
BEGIN

    -- new column was updated, so trigger writes to old column
   IF NEW.height_cm <> 0 AND NEW.height = '0' THEN
       NEW.height = cast(NEW.height_cm as TEXT);

   -- old column was updated, so trigger writes to new column
   ELSIF NEW.height_cm = 0 AND NEW.height <> '0' THEN
       NEW.height_cm = cast(NEW.height as INT);

   END IF;

   RETURN NEW;

END;
$BODY$
LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION sync_height_on_update() RETURNS TRIGGER AS
$BODY$
BEGIN

   -- new column was updated, so trigger writes to old column
   IF NEW.height_cm <> OLD.height_cm THEN
       NEW.height = cast(NEW.height_cm as TEXT);

   -- old column was updated, so trigger writes to new column
   ELSIF NEW.height <> OLD.height THEN
       NEW.height_cm = cast(NEW.height as INT);

   END IF;

   RETURN NEW;

END;
$BODY$
LANGUAGE plpgsql;


CREATE TRIGGER trigger_sync_height_on_update
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION sync_height_on_update();

CREATE TRIGGER trigger_sync_height_on_insert
    BEFORE INSERT ON users
    FOR EACH ROW
    EXECUTE FUNCTION sync_height_on_insert();
