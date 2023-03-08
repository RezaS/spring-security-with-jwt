# Spring Security with JWT #

This repository contains an example project that demonstrates how to implement Spring Security with JSON Web Tokens (JWT) in a Spring Boot application. The project uses Spring Security to handle authentication and authorization, and JWTs to secure RESTful endpoints. This repository is great for cloning for any new Spring Boot project. 

## Getting started ##

To run this project, you will need to have Java 8 or higher installed on your machine. You will also need to have Maven installed, which you can download from the [official website](https://maven.apache.org/download.cgi).

### Setting up the database ###
This project requires a PostgreSQL database to be set up. If you do not have PostgreSQL installed on your machine, you can download it from the [official website](https://www.postgresql.org/download/).

Once you have installed PostgreSQL, you will need to create a database for this project. You can do this by running the following command in a PostgreSQL shell:

`CREATE DATABASE my_database;`

Finally, you will need to create the tables required for this project. You can do this by running the following SQL code in the PostgreSQL shell:

```POSTGRESQL
create table users(
                      username varchar(50) not null primary key,
                      password varchar(100) not null,
                      enabled boolean not null
);

create table authorities (
                             username varchar(50) not null,
                             authority varchar(50) not null,
                             constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);
```

#### Modifying application.yml ####

Before running the application, you will need to modify the application.yml file to match your PostgreSQL database configuration. So you probably may need to change the URL, database name, username, and password values.

### Running the application ###

To start the application, simply run the following command from the root directory of the project:

`mvn spring-boot:run`

This will start the Spring Boot application on port 8080. You can then access the application by navigating to http://localhost:8080 in your web browser.

## API endpoints ##

The following API endpoints are available in this application:

### Home ###
- **Endpoint:** /api/home
- **HTTP Method:** GET
- **Description:** This endpoint is just an example controller for testing. It requires no authorization and no authentication.

### Register User ###
- **Endpoint:** /api/register
- **HTTP Method:** POST
- **Description:** This endpoint will register a new user.
- **Request Body**: `username` and `password` are required. See example:
```JSON
  {
  "username": "name@example.com",
  "password": "12345678"
  }
```

### User Login ###
- **Endpoint:** /api/login
- **HTTP Method:** POST
- **Description:** This endpoint allows users to log in and obtain an authentication token.
- **Request Body**: `username` and `password` are required. See example:
```JSON
  {
  "username": "name@example.com",
  "password": "12345678"
  }
```

### Welcome ###
- **Endpoint:** /api/welcome
- **HTTP Method:** GET
- **Description:** This endpoint is only accessible by registered and logged-in users.
- **Request Headers:**
```
  Authorization: Bearer <token>
```
Replace `<token>` with the token obtained from the _/api/login_ endpoint.