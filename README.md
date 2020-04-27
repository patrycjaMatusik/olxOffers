# Olx

![Vertx image](https://img.shields.io/badge/vert.x-3.9.0-purple.svg)

This application was generated using http://start.vertx.io

## Building

To launch your tests:
```
./mvnw clean test
```

To package your application:
```
./mvnw clean package
```

To run your application:
```
./mvnw clean compile exec:java
```
## Description

Application purpose is to get keyword from user, search for this keyword on olx.pl and fetch first page of offers. These offers should be parsed to JSON format and displayed to user.
Application can be ran from IDE (main method is in MainVerticle class). Second option is using maven.

Simple user interface is available:

![UI view](/src/main/resources/img/front.png)

### Steps:
1. go to localhost:8888/
1. type in keyword you want to search for
1. click 'search' button
1. result is displayed in textarea Result


