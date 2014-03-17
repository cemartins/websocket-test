package org.juffrou.test.websocket;

import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

@ServerEndpoint(value="/wstest", configurator = SpringConfigurator.class)
public class MyEndpoint {

	@Autowired
	MyService myService;
	
	public MyEndpoint() {
		System.out.println("MyEndpoint instanciated");
	}
	
	@OnOpen
	public void myOnOpen(Session session, EndpointConfig config) {
		System.out.println("websocket connection opened");
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		if (wac == null) {
			String message = "Failed to find the root WebApplicationContext. Was ContextLoaderListener not used?";
			throw new IllegalStateException(message);
		}
		myService = wac.getBean(MyService.class);
		if(myService != null)
			System.out.println("Success");
		else
			System.out.println("Can't find bean");
	}
	
	@OnMessage
	public String myOnMessage(String message, Session session) {
		System.out.println("message received");
		return myService == null ? "myService is null" : myService.getGreeting() + " " + message;
	}
}
