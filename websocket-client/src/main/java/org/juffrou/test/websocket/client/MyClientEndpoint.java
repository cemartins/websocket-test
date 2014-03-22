package org.juffrou.test.websocket.client;

import java.io.IOException;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.juffrou.test.WebsocketClientTest;

public class MyClientEndpoint extends Endpoint {

	private static final String SENT_MESSAGE = "Hello World";
	
	@Override
	public void onOpen(Session session, EndpointConfig config) {
		session.addMessageHandler(new MyMessageHandler());
		
		try {
			session.getBasicRemote().sendText(SENT_MESSAGE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class MyMessageHandler implements MessageHandler.Whole<String> {

		@Override
		public void onMessage(String message) {
			System.out.println("Received message: "+message);
			WebsocketClientTest.messageLatch.countDown();
		}
	}
}
