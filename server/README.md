# ZDD - Zero Downtime Deployment

This is a project to illustrate zero downtime deployments.

## Setup

Software that needs to be installed:

* Java 12 (use OpenJDK)
* PostgreSQL 11 (via docker, see below)
* Gradle (via gradle wrapper, see below)

### Database

#### Setup

To make it easy for development, we can use a docker container to run Postgres.
Some postgres-on-docker steps are here: https://hackernoon.com/dont-install-postgres-docker-pull-postgres-bee20e200198


--------------
Install Docker
--------------

On Linux: `sudo apt install docker.io`
Note: On Linux, needed to run docker as sudo.
docker daemon must run as root, but you can specify that a group other than docker should own the Unix socket with the -G option.

On Mac: can install Docker Desktop from docker hub, or use brew


----------------
Set up Docker PG
----------------

Then pull the docker image: `docker pull postgres`

use different host port in case there are other Postgres instances running on the host
POSTGRES_PASSWORD is the password for the default admin "postgres" user
`docker run --rm --name pg-docker -d -e POSTGRES_PASSWORD=postgres -d -p 5555:5432 postgres`
`docker container ls`


----------------
Prepare DB
----------------

After starting the container run create-db.sql 
Flyway connects to an existing database in a transaction,
and creating a database is outside a transaction, so db creation should be part of setup.

If you have PG on the host: `psql -h localhost -U postgres -d app -p 5555`

So you can just call from the host:
`psql -h localhost -p 5555 -U postgres -f db/create-db.sql`

Or, you should be able to access the database from within docker:
`docker exec -it pg-docker psql -U postgres`

So you can just call from the host:
`docker exec -it pg-docker psql -U postgres --command="CREATE DATABASE app OWNER postgres ENCODING 'UTF8';"`


----------------
Blow away and rebuild DB
----------------

Blow away the whole database and start from scratch
`docker container stop pg-docker`
`docker run --rm   --name pg-docker -e POSTGRES_PASSWORD=postgres -d -p 5555:5432 postgres`
`docker exec -it pg-docker psql -U postgres --command="CREATE DATABASE app OWNER postgres ENCODING 'UTF8';"`


#### Migrations

We use Flyway: https://flywaydb.org/getstarted/firststeps/gradle

Flyway as run from gradle doesn't by default use the database connection info in the properties file
It uses the database connection info in the "flyway" block in build.gradle
But we can load the properties from application.properties so we only have to define them in one place.

When performing migrations, make sure the uuid extension is available to the schema
e.g. `set search_path = my_schema, extensions;`

Drop all tables on managed schemas: `gradlew flywayClean -i`
Run all the migrations: `gradlew flywayMigrate -i`
Drop and run all migrations: `gradlew flywayClean; gradlew flywayMigrate -i`

This project uses the pgcrypto extension to create cryptographically secure UUIDs.
Can test with PSQL: `select cast (gen_random_uuid() as varchar(36));`

We run the migration standalone (not on startup of the application)
So that we have more control over the migration process.



### HTTPS

To make self-signed keys for dev:
`keytool -genkeypair -alias app -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore app.dev.p12 -validity 3650`

To update HTTPS related files and properties, see the `server.ssl.*` properties used by Spring Boot

## Running

If starting with a new run of docker, need to ensure the migrations have been run
since they don't run automatically on app startup. See migration steps.

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

Actuator (admin/management endpoints) enpoints are listed at
`https://localhost:8080/actuator`

For example, try /actuator/health


## Cloud (Heroku)
 
Heroku requires apps to bind a port in 60s or it's considered crashed
https://devcenter.heroku.com/changelog-items/364
migrations can eat into that time, there are ways to move that out
