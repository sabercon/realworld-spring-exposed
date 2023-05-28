# ![RealWorld Spring Exposed](logo.png)

> ### Kotlin + Spring codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.

### [Demo](https://demo.realworld.io/)&nbsp;&nbsp;&nbsp;&nbsp;[RealWorld](https://github.com/gothinkster/realworld)

This codebase was created to demonstrate a fully fledged fullstack application built with **Kotlin + Spring** including
CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the **Kotlin + Spring** community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to
the [RealWorld](https://github.com/gothinkster/realworld) repo.

# How it works

- Spring Boot for Web API implementations
- Exposed for persistence layer
- Flyway for database migrations
- PostgreSQL for actual database
- JWT for authentication
- Kotest for tests
- Testcontainers for integration tests
- Detekt for static code analysis
- Kover for test coverage

## Security

Instead of using Spring Security to implement an authenticator using JWT,
I added a simple extension method to `ServerRequest` to verify the token and get the user ID.

The secret key is stored in [application.yml](src/main/resources/application-dev.yml).

## Database

When running locally, the application will start a PostgreSQL database using [docker-compose.yml](docker-compose.yml).

# Getting started

## Running the application locally

- Make sure you have Java 17 and Docker installed
- Run `./gradlew bootRun` to start the application and the database
- You can now access the application at `http://localhost:8080/api`

## Running tests

### Using shell script

```shell
$ ./api/run-api-tests.sh
```

### Using gradle test

```shell
$ ./gradlew test
```
