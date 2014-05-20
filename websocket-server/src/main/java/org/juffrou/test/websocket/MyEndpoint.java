package org.juffrou.test.websocket;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyEndpoint extends Endpoint {

	@Autowired
	private MyService myService;
	
	public MyEndpoint() {
		System.out.println("SERVER ENDPOINT INSTANCIATED");
	}
	
	@Override
	public void onOpen(Session session, EndpointConfig config) {
		
		System.out.println("websocket connection opened: " + session.getId());
		
		session.addMessageHandler(new MyMessageHandler(session));
	}
	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		super.onClose(session, closeReason);
		System.out.println("websocket connection closed: " + session.getId());
	}
	
	@Override
	public void onError(Session session, Throwable thr) {
		System.out.println("websocket connection exception: " + session.getId() + " threw " + thr.getClass().getSimpleName());
	}
	
	class MyMessageHandler implements MessageHandler.Whole<String> {

		final Session session;
		
		public MyMessageHandler(Session session) {
			this.session = session;
		}
		
		@Override
		public void onMessage(String message) {
			try {
				String greeting = myService.getGreeting();
				session.getBasicRemote().sendText(greeting + ", got your message (" + message + "). Thanks!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
