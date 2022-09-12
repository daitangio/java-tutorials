# Spring WebSockets

This module contains articles about Spring WebSockets.

## Introduction
Refer to the [Spring reference manual](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/web.html#websocket) for an introduction.

This demo is based to Websocket + STOMP protocol.
The project enable a user to "connect" to a bi-directional socket and to receive asyncronous notification.
In this example the notification are time-based, and randomly select a user.

## Known issues: authentication 
We must relay on Token Authentication as described here
https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/web.html#websocket-stomp-authentication

Refer to the specific spring security integration for better implementations:
https://docs.spring.io/spring-security/reference/servlet/integrations/websocket.html#websocket-authorization


The current PoC is very simple and trut the client "login" header.

See also https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/web.html#websocket-stomp-handle-broker-relay-configure


## Scheduled WebSocket Push with Spring Boot

To test this open the bots.html with
http://localhost:8080/bots.html

How to run

    > ./mvnw  compile spring-boot:run


# Changelog
Adapted to work on JDK 17, kept only one example called "sendtouser", extended and 
added ability to increase logging levels.


### Relevant articles
- [Intro to WebSockets with Spring](https://www.baeldung.com/websockets-spring)
- [A Quick Example of Spring Websockets’ @SendToUser Annotation](https://www.baeldung.com/spring-websockets-sendtouser)
- [Scheduled WebSocket Push with Spring Boot](https://www.baeldung.com/spring-boot-scheduled-websocket) 
- [Test WebSocket APIs With Postman](https://www.baeldung.com/postman-websocket-apis)
- [Debugging WebSockets](https://www.baeldung.com/debug-websockets)
