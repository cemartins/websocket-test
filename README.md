# websocket-test
==================

Small webapp to demonstrate one way to setup websockets with spring framework version 3.x.

Please read the blog post http://makeitmartins.blogspot.pt/2014/10/javafx2-websocket-client-deployed-by.html for a more detailed introduction.

This application sets up a websocket server endpoint with uri `/wstest` which will use a `@Autowired` spring bean
to select a greeting word and reply to a websocket message.

The websocket connection is initiated and the messages are sent by either an html page (`index.html`) running in a browser that supports websockets or by 
a small JavaFx application lauched by java webstart.

## How does it work
-------------------

It works around the Servlet container's scan for WebSocket endpoints by not using the `@ServerEndpoint` annotation and instead
implementing a ServerEndpointConfig and adding it to the server container upon servlet context initialization.

This way, the endpoint instance will be provided by SpringConfigurator, which means it can itself be a spring bean and/or it
can have spring dependencies automatically injected with the `@Autowired` annotation.

## Maven - building and running
-------------------------------

### Prerequisites:

JDK 8.0.5+

Code signing certificate

Because of Java Web Start security, you need to get a code signing certificate and create a profile in your maven settings.xml file with the following properties:

```xml
<profile>
	<id>mycert</id>
	<properties>
		<keystore.dir>PATH/TO/FOLDER/CONTAINING/KEYSTORE</keystore.dir>
		<keystore.file>KEYSTORE_FILENAME</keystore.file>
		<keystore.type>KEYSTORE_TYPE (ex. pkcs12)</keystore.type>
		<keystore.pass>KEYSTORE_PASSWORD</keystore.pass>
		<certificate.alias>CERTIFICATE_ALIAS</certificate.alias>
		<certificate.pass>CERTIFICATE_PASSWORD</certificate.pass>
	</properties>
</profile>
```

This will allow you to sign your application with a verifiable certificate.

For this project you can use a free open source code signing certificate from certum (http://certum.eu)

To build the `websocket-server.war` file execute the maven command `mvn -Pmycert package` (use the profile you created in settings.xml).

### Jetty

You can run the webapp with jetty with the maven command `mvn jetty:deploy-war -f websocket-server/pom.xml`
* start your browser and access the url `http://localhost:8080/websocket-test/index.html`
* choose between HTML5 web page client or a JavaFX 2 rich-client application (The JavaFX 2 application will be launched by java web start after downloading the descriptor websocket-client.jnlp)
* follow the directions.

To start the client application in debug mode, execute javaws -J-Xdebug -J-Xnoagent -J-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=65099 "$HOME/Downloads/websocket-client.jnlp"

You can also launch the JavaFx Application directly from maven with the command `mvn exec:java -f websocket-client/pom.xml`

### WildFly 8

You can also deploy and run websocket-test in WildFly 8:
* add `websocket-test.war` to `WILDFLY_HOME/standalone/deployments
* start WildFly 8
* start your browser and access the url `http://localhost:8080/websocket-test/index.html`
* type a message, press the button "Send" and see the response message.

Happy testing :)

Note: SpringConfigurator was developed by Rossen Stoyanchev and introduced in spring-core version 4.0.