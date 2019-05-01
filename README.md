# ZDD - Zero Downtime Deployment

This is a project to illustrate zero downtime deployments.

## Setup

Software that needs to be installed:

* Java 11
* PostgreSQL 11

Gradle is run via gradle wrapper.

### Database

Postgres should be installed with a user named "postgres".
You should be able to access the database with `psql -U postgres` 

Database on docker
sudo apt install docker.io (maybe don’t install as sudo? All docker commands after this have to be sudo)
sudo docker pull postgres
docker run --rm   --name pg-docker -e POSTGRES_PASSWORD=postgres -d -p 5555:5432  postgres
(from another command line) psql -h localhost -U postgres -d postgres -p 5555
(see it running) docker container ls
Stop (might not preserve data) sudo docker container stop pg-docker

Can we create users from command line? Install extensions/etc? Or does that need to be baked in to the docker file and image?
Creating the database is SQL, and that can be executed by psql. Just can’t be executed in a transaction from flyway



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

We run the migration standalone (not on startup of the application)
So that we have more control over the migration process.

https://flywaydb.org/getstarted/firststeps/gradle


### HTTPS

To make self-signed keys for dev:
`keytool -genkeypair -alias app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore app.dev.p12 -validity 3650`

To update HTTPS related files and properties, see the `server.ssl.*` properties used by Spring Boot

## Running

Run `gradlew bootRun`, or run `gradlew cleanRun` to clear the database and run the server in one step

## Debugging

Right click the main class and "Debug Application (main)"

## Testing

### Unit test
 
run `gradlew test`

Code coverage metrics with Jacoco
`gradlew test jacocoTestReport`
Then see output in build/reports/jacoco/html/index.html

### Manual test

curl quick guide: https://gist.github.com/subfuzion/08c5d85437d5d4f00e58

Run the server, then from another command line run `curl -k https://localhost:8080/user`

post:
`curl -k -X POST -H "Content-Type: application/json" -d '{"username":"user1", "displayName":"user1", "email":"us@r.com"}' https://localhost:8080/user`
or if the json is in a file:
`curl -k -X POST -H "Content-Type: application/json" -d @data-file.json https://localhost:8080/user`


## Cloud (Heroku)
 
Heroku requires apps to bind a port in 60s or it's considered crashed
https://devcenter.heroku.com/changelog-items/364
migrations can eat into that time, there are ways to move that out


## TODO

* review and refresh README (database steps)

* introduce security
security.require-ssl=true

* make registration process


