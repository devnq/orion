# Orion

A practical demonstration of Software Testing in a Web App and RESTful Service.

## Server

[README](server/README.md)

A Java 8 service bootstrapped by NinjaFramework. It is configured with:

* DependencyInjection (DI);
* SQL Persistence Layers (H2 for memory testing) with per request transaction isolation;
* Basic Annotated `authorization` and auth user extraction;
* JSON Schema data validation;
* Immutables; and
* Examples of Unit, Business Logic, End-to-End and Mock testing.

## Web

[README](web/README.md)

A Mithril Single Page Application (SPA) for the Orion Server

* React like view rendering;
* Rudimentary route protection and login redirection;
* Examples of Unit, API and Mock testing.

## Functional-Tests

[README](functional-tests/README.md)

A Selenium Webdriver Automation Tests

* Browser automation showing a complete test of the system as a whole
