# ZDD - Zero Downtime Deployment

This is a project to illustrate zero downtime deployments.

## Setup

[Setup for server project](server/README.md)


## Project Structure

There are two sub-projects: server and perf.

The server project is a web server for a normal web application.
The perf project is a basic load testing application that runs against the server.


### Running with Gradle

Both can be run with Gradle from the base (current) folder.

For example:
`gradlew :server:bootRun` is equivalent to `gradlew -p server bootRun`

#### Showing Blue Green Deployment

To override the port so we can run multiple servers at once
e.g. 
`gradlew :server:flywayMigrate -i`
`gradlew :server:bootRun --args='--server.port=9001'`
`gradlew :perf:bootRun --args='--connect.port=9001'`


### Running from IDE

TODO show how to run from IDE
TODO show how to run in debugger

### Monitoring

[VisualVM](https://visualvm.github.io/) is a handy monitoring tool,
you can download it and run with Java 11.

To show that a server is under load, open the running application in VisualVM
and click on the threads tab. Note the threads named something like
`https-jsee-nio-9000-exec-1` and `https-jsee-nio-9000-exec-2`. These are 
the request handling threads, if they are green they are running. 
If they are both solid orange, the thread is parked and the server is not
actively handling any requests.

## Branch Procedures

Define acceptance criteria so we know what is in scope.
Create branch locally and push to remote


## Merge Procedures

Ensure acceptance criteria are met.

Update README docs as necessary.

Always run a full build with test coverage before merging a branch.
We can do this from the base folder with
`gradlew clean build :server:jacocoTestReport` 

Do a squash merge so master contains a single commit per issue

