package org.juffrou.test.websocket;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class MyEndpoint extends Endpoint {

	@Autowired
	MyService myService;
	
	public MyEndpoint() {
		System.out.println("MyEndpoint instanciated");
	}
	
	@Override
	public void onOpen(Session session, EndpointConfig config) {
		
		System.out.println("websocket connection opened");
		
		session.addMessageHandler(new MyMessageHandler(session));
	}
	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		super.onClose(session, closeReason);
		System.out.println("websocket connection closed");
	}
	
	class MyMessageHandler implements MessageHandler.Whole<String> {

		final Session session;
		
		public MyMessageHandler(Session session) {
			this.session = session;
		}
		
		@Override
		public void onMessage(String message) {
			try {
				session.getBasicRemote().sendText("Got your message (" + message + "). Thanks !");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
