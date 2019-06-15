set search_path TO public;




-- triggers aren't being used any more, drop those first
DROP TRIGGER IF EXISTS trigger_sync_height_on_update ON users;
DROP TRIGGER IF EXISTS trigger_sync_height_on_insert ON users;

-- then drop the functions which aren't used any more
DROP FUNCTION IF EXISTS sync_height_on_update;
DROP FUNCTION IF EXISTS sync_height_on_insert;

-- finally drop the column which isn't used any more
ALTER TABLE users DROP COLUMN height;
