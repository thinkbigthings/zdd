# ZDD - Zero Downtime Deployment

This is a project to illustrate zero downtime deployments.

## Setup

Software that needs to be installed:

* Java 11
* PostgreSQL 9.6.9

Gradle is run via gradle wrapper.

## Database

For the first time setup, with PG installed, run create-db.sql and extensions.sql
`psql -U postgres -f db/create-db.sql`
`psql -U postgres -f db/extensions.sql`

When performing migrations, make sure the uuid extension is available to the schema
e.g. `set search_path = my_schema, extensions;`

## Testing

run `gradlew bootRun`, then from the command line:

`curl http://localhost:8080/user`
