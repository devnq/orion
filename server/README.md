# Orion Server

## Running

**Running Tests**

> Note: All test should run with the timezone set as UTC (because events use epoch values)
> set via the JVM flag `-Duser.timezone=UTC`

From the command line, within the `server` directory:
```bash
$ mvn test
```

IntelliJ should be able to run these tests without any extra configuration.

**Running the Server**

```bash
$ mvn clean
$ mvn compile
$ mvn ninja:run
```

## DevNQ August 2019 Session

Orion Server is an example of UnitTesting within a stateless RESTful Java server. There are examples of:

* UnitTesting (DAL, Utils);
* Mocking (Filters, Extractors);
* End-to-End Testing (API);

Not all tests are complete; and are left failing for you to investigate. Can you fix/implement the failing tests?

### Structure
```
.
└── src
    ├── main
    │   ├── java
    │   │   ├── conf                                  <- Root level application configuration
    │   │   │   ├── Filters.java                      <- Dependency Injection of per-request Filters
    │   │   │   ├── Module.java                       <- Dependency Injection of Modules (DB, Flyway, StartupActions)
    │   │   │   ├── Ninja.java                        <- NinjaFramework extensions for Exception responses
    │   │   │   ├── Routes.java                       <- API Route Definition
    │   │   │   ├── StartupActions.java               <- Once off Server startup configuration
    │   │   │   ├── application.conf                  <- Environment variables
    │   │   │   ├── messages.properties               <- Translation files
    │   │   │   └── messages_de.properties
    │   │   ├── logback.xml                           <- Logging configuration
    │   │   └── org
    │   │       └── devnq
    │   │           └── orion
    │   │               └── server
    │   │                   ├── api                   <- Request handling (endpoint definitions)
    │   │                   │   ├── Auth.java   
    │   │                   │   ├── Events.java
    │   │                   │   └── Users.java
    │   │                   ├── crypt                 <- Cryptography utility functions
    │   │                   │   ├── BCrypt.java
    │   │                   │   └── Tokens.java
    │   │                   ├── dal                   <- DataAccessLayer implementation
    │   │                   │   ├── EventDal.java
    │   │                   │   ├── UserDal.java
    │   │                   │   └── UserDalUtils.java
    │   │                   ├── etc                   <- Misc runtime annotations and request argument extractors
    │   │                   │   ├── AuthUser.java
    │   │                   │   ├── AuthUserExtractor.java
    │   │                   │   ├── Authenticated.java
    │   │                   │   ├── JsonBodySchema.java
    │   │                   │   ├── JsonSchema.java
    │   │                   │   ├── JsonSchemaExtractor.java
    │   │                   │   ├── TokenDecodeException.java
    │   │                   │   └── UnauthorizedException.java
    │   │                   ├── filters               <- Per-request filters (run before endpoint definition). Good for validation/authorization
    │   │                   │   └── AuthenticationFilter.java
    │   │                   ├── json                  <- JSON processing utilities
    │   │                   │   ├── Schema.java
    │   │                   │   └── Util.java
    │   │                   └── models                <- API Models and DataTransferObjects (DTO)
    │   │                       ├── Event.java
    │   │                       ├── Login.java
    │   │                       └── User.java
    │   └── resources                                 <- Files compiled into the final runnable JAR
    │       ├── json-schema                           <- JSON Schema definitions (for request body validation)
    │       │   ├── Login.json
    │       │   ├── NewEvent.json
    │       │   └── NewUser.json
    │       └── migrations                            <- Flyway Database Migrations
    │           └── default
    │               ├── V1__init_users_table.sql
    │               └── V2__init_events_table.sql
    └── test
        ├── java
        │   └── org
        │       └── devnq
        │           └── orion
        │               └── server
        │                   ├── ApiTest.java          <- Base test class for API tests (has server run configured)
        │                   ├── DalTest.java          <- Base test class for DAL tests (has DI configured)
        │                   ├── Generators.java       <- API Model generators
        │                   ├── api                   <- API end-to-end tests
        │                   │   ├── AuthApiTest.java
        │                   │   ├── EventsApiTest.java
        │                   │   └── UsersApiTest.java
        │                   ├── crypt
        │                   │   ├── BCryptTest.java
        │                   │   └── TokensTest.java
        │                   ├── dal                   <- DataAccessLayer tests (db query testing)
        │                   │   ├── EventDalTest.java
        │                   │   └── UserDalTest.java
        │                   ├── etc
        │                   │   └── AuthUserExtractorTest.java
        │                   ├── filters
        │                   │   └── AuthenticationFilterTest.java
        │                   ├── generators
        │                   └── json                 <- API model validation testing
        │                       ├── LoginSchemaTest.java
        │                       └── NewUserSchemaTest.java
        └── resources
            └── logback-test.xml                     <- Logging configuration used while running tests
```

### Frameworks/Dependencies Used

#### Production Code

##### [NinjaFramework](https://www.ninjaframework.org)

> Ninja is a full stack web framework for Java. Rock solid, fast, and super productive.

The Ninja web framework provided the boilerplate configuration for the service including DependencyInjection, Routing,
Content Negotiation, and Request Handling.

##### [Ninja-db](https://github.com/ninjaframework/ninja-db)

The Ninja-db module provides additional relational database support on-top of the web framework. The boilerplate comes
pre-configured with Java Persistence API (JPA). The JPA, while feature complete and capable, often lacks the fine grain
query capability and model validity. It also doesn't play nicely with Immutable data structures.

##### [Flyway](https://flywaydb.org/)

Flyway is a Database migration tool that gives us the ability to commit Database Schema with the code; and update any
live environments with confidence. Never manually modify the database schema again!

##### [Immutables](https://immutables.github.io/)

Immutables is a java annotation library which provides processors to generate simple and safe immutable objects from
simple interface or abstract class definitions.

##### [JSON Schema Validator](https://github.com/everit-org/json-schema)

Used to validate incoming request content against pre-defined schemas. Any errors in the request are bounced back to the
requester before they reach the endpoint handlers. This approach is used in an attempt to prevent malformed requests
(ultimately simplifying the request handling).

#### Test Code

##### [JUnit5](https://junit.org/junit5/)

A widely used Java testing foundation for the JVM. Simple to use, and supported by IntelliJ out of the box.

##### [Mockito](https://site.mockito.org/)

> Tasty mocking framework for unit tests in Java

Mockito is used in test scenarios where it may be difficult or tedious to produce test cases. Java Objects can be mocked
with their method calls stubbed to return any result needed. Mockito also provides verification and spy capabilities so
we can be confident that our objects are only calling what needs to be called.


##### [AssertJ](https://joel-costigliola.github.io/assertj/)

AssertJ is used in almost all of the test files to provide easy to read, and fluent assertion definitions. Highly 
recommended. 