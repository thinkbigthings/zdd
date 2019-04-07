#!/usr/bin/env bash

# Die on error.
set -e

# echo commands
set -o xtrace

# Usage to override properties in pom:
#   bash clean.sh localhost 5432 app postgres postgres
#   e.g.   ./clean.sh localhost 5432 app postgres postgres

export PGPASSWORD=$5
psql -h $1 -p $2 -U $4 $3 -t -c "select 'drop table \"' || schemaname || '\".\"' || tablename || '\" cascade;' from pg_tables where schemaname != 'pg_catalog' AND schemaname != 'information_schema'" | psql -h $1 -p $2 -U $4 $3
psql -h $1 -p $2 -U $4 $3 -t -c "select 'drop sequence \"' || relname || '\" cascade;' from pg_class where relkind='S'" | psql -h $1 -p $2 -U $4 $3

