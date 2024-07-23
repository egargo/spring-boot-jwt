# Spring Boot JWT

Spring Boot JWT

> [!NOTE]
> Finished this yesterday afternoon but was too anxious about the code quality so I kept on refactoring the code

## Contents

- [Dependencies](#dependencies)
- [Setup](#setup)
- [Run](#run)
- [JWT Process](#jwt-process)
- [Code Structure Documentation](#code-structure-documentation)
- [DB Structure](#db-structure)

## Dependencies

- [SDKMAN!](https://sdkman.io/)
    - Java
    - Maven

## Setup

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java
sdk install maven
```

## Run

```bash
mvn spring-boot:run
```

## JWT Process

The API generates `accessToken` and `refreshToken` on successful user login.
The `accessToken` will be used to access protected routes.
The `refreshToken` will be used to refresh the access token when the current one expires.

The `accessToken` will be stored in the `Authorization` header.
The `refreshToken` will be stored in the custom `refreshToken` header.

The configuration for JWT secret key, access and refresh token expiration time are in the `src/main/resources/application.properties`.


## Code Structure Documentation

- Controller
    - `AuthController.java`
        Contains the request/response controller for authentication routes

- Model
    - `User.java`
        Maps the users table in the database
        Holds the data
    - `UserLogin.java`
        Holds the user login details

- Repository
    - `UserRepository.java`
        Contains the functions to read/write data to the database

- Services
    - `AuthStrategy.java`
        Contains the interface for `AuthStrategyImpl`
    - `AuthStrategyImpl.java`
        Contains the implementation of `AuthStrategy` interface
    - `JwtService.java`
        Contains the methods to generate and verify JWT
    - `UserService.java`
        Contains the methods for basic user CRUD operations

## DB Structure

Test DB structure

```bash
CREATE DATABASE test;

USE test;

CREATE TABLE IF NOT EXISTS users(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    email VARCHAR(128) NOT NULL,
    password VARCHAR(255) NOT NULL
);
```
