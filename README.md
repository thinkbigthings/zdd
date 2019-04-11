# ZDD - Zero Downtime Deployment

This is a project to illustrate zero downtime deployments.

## Setup

Software that needs to be installed:

* Java 11
* PostgreSQL 9.6.9

Gradle is run via gradle wrapper.

## Database

For the first time setup, with PG installed, run create-db.sql 
Flyway connects to an existing database in a transaction,
and creating a database is outside a transaction, so db creation should be part of setup.
 

`psql -U postgres -f db/create-db.sql`

drop database for real
`psql -U postgres -f db/drop-db.sql`

When performing migrations, make sure the uuid extension is available to the schema
e.g. `set search_path = my_schema, extensions;`

Drop all tables: `gradlew flywayClean -i`
Run all the migrations: `gradlew flywayMigrate -i`
Drop and run all migrations: `gradlew flywayClean; gradlew flywayMigrate -i`

This project uses the pgcrypto extension to create cryptographically secure UUIDs.
Can test with PSQL: `select cast (gen_random_uuid() as varchar(36));`

We are able to run the migration standalone (auto configured on startup, but able to do manual first)
https://flywaydb.org/getstarted/firststeps/gradle
https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html



## Testing

Unit test: run `gradlew test`

Manual test: run `gradlew bootRun`, then from another command line run `curl http://localhost:8080/user`

curl quickguide: https://gist.github.com/subfuzion/08c5d85437d5d4f00e58

post:
`curl -X POST -H "Content-Type: application/json" -d '{"username":"user1", "displayName":"user1", "email":"us@r.com"}' http://localhost:8080/user`
or if the json is in a file:
`curl -X POST -H "Content-Type: application/json" -d @data-file.json http://localhost:8080/user`

    
## Cloud (Heroku)

Heroku requires apps to bind a port in 60s or it's considered crashed
https://devcenter.heroku.com/changelog-items/364
migrations can eat into that time, there are ways to move that out


## TODO

* review and refresh README
* introduce security
* make registration process


