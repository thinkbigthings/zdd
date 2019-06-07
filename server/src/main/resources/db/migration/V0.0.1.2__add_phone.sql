
set search_path TO public;


ALTER TABLE users ADD COLUMN phone_number VARCHAR DEFAULT '';

-- there should be no new null values, so can populate old null values now
UPDATE users SET phone_number='' WHERE phone_number IS NULL;

-- finally can add not null constraint
ALTER TABLE users ALTER COLUMN phone_number SET NOT NULL;
