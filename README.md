# ZDD - Zero Downtime Deployment

This is a project to illustrate zero downtime deployments.

## Setup

[Setup for server project](server/README.md)


## Project Structure

There are two sub-projects: server and perf.


### Running with Gradle

Both can be run with Gradle from the base (current) folder.

For example:

`gradlew :server:cleanRun` is equivalent to `gradlew -p server cleanRun`

With the server started up, we can run a performance test with
`gradlew -p perf run`

### Running from IDE

Each class that has a main() method can be run (right-click -> run) from inside an IDE.


## Merge Procedures

Always run a full build with test coverage before merging a branch.

We can do this from the base folder with
`gradlew clean build :server:jacocoTestReport` 