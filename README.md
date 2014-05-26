websocket-test
==============

Small webapp to demonstrate one way to setup websockets with spring framework version 3.x.

This application sets up a websocket server endpoint with uri `/wstest` which will use a `@Autowired` spring bean
to select a greeting word and reply to a websocket message.

The websocket connection is initiated and the messages are sent by either an html page (`index.html`) running in a browser that supports websockets or by 
a small JavaFx application lauched by webstart.

How does it work
----------------

It works around the Servlet container's scan for WebSocket endpoints by not using the `@ServerEndpoint` annotation and instead
implementing a ServerEndpointConfig and adding it to the server container upon servlet context initialization.

This way, the endpoint instance will be provided by SpringConfigurator, which means it can itself be a spring bean and/or it
can have spring dependencies automatically injected with the `@Autowired` annotation.

Maven - building and running
----------------------------

To build the `websocket-test.war` file execute the maven command `mvn package`.

You can run the webapp with jetty with the maven command `mvn jetty:run-exploded -f websocket-server/pom.xml`
* start your browser and access the url `http://localhost:8080/websocket-test/index.html`
* choose between html page client or a JavaFx App client;
* follow the directions.

You can also launch the JavaFx App directly from maven with the command `mvn exec:java -f websocket-client/pom.xml`

WildFly 8
---------

You can also deploy and run websocket-test in WildFly 8:
* add `websocket-test.war` to `WILDFLY_HOME/standalone/deployments
* start WildFly 8
* start your browser and access the url `http://localhost:8080/websocket-test/index.html`
* type a message, press the button "Send" and see the response message.

Happy testing :)

Note: SpringConfigurator was developed by Rossen Stoyanchev and introduced in spring-core version 4.0.