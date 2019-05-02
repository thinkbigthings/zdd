
-- Create extensions schema to hold all extensions
CREATE SCHEMA extensions;

---- make sure everybody can use everything in the extensions schema
--GRANT USAGE ON SCHEMA extensions TO PUBLIC;
--GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA extensions TO PUBLIC;
--
---- include future extensions
--ALTER DEFAULT PRIVILEGES IN SCHEMA extensions
--GRANT EXECUTE ON FUNCTIONS TO PUBLIC;
--
--ALTER DEFAULT PRIVILEGES IN SCHEMA extensions
--GRANT USAGE ON TYPES TO PUBLIC;

CREATE EXTENSION IF NOT EXISTS "pgcrypto" SCHEMA extensions;

