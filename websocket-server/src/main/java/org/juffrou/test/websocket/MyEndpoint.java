package org.juffrou.test.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.PongMessage;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyEndpoint extends Endpoint {
	
	private static final ByteBuffer pongload = ByteBuffer.wrap("asdfghjkl".getBytes(Charset.defaultCharset()));

	@Autowired
	private MyService myService;
	private String name = "";
	private String age = "unknown";
	private String locatedAt = "noWHERE";
	
	public MyEndpoint() {
		System.out.println("SERVER ENDPOINT INSTANCIATED");
	}
	
	@Override
	public void onOpen(Session session, EndpointConfig config) {
		
		System.out.println("websocket connection opened: " + session.getId());
		
		MyPongHandler pongHandler = new MyPongHandler(session);
		session.addMessageHandler(new MyMessageHandler(session, pongHandler));
		session.addMessageHandler(pongHandler);
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
		final MyPongHandler pongHandler;
		
		public MyMessageHandler(Session session, MyPongHandler pongHandler) {
			this.session = session;
			this.pongHandler = pongHandler;
		}
		
		@Override
		public void onMessage(String message) {
			
			if ("STOPPONGING".equals(message))
				pongHandler.setResponding(false);
			else if ("STARTPONGING".equals(message)) {
				pongHandler.setResponding(true);
				try {
					session.getAsyncRemote().sendPong(pongload);
				} catch (IllegalArgumentException | IOException e) {
					e.printStackTrace();
				}
			} else {
				Map<String, String> pathParameters = session.getPathParameters();
				name = pathParameters.get("name");
				
				Map<String, String> queryParams = getQueryMap(session.getQueryString());
		        if (queryParams.containsKey("age")) {
		        	age = queryParams.get("age");
		        }		        
		        if (queryParams.containsKey("locatedAt")) {
		        	locatedAt = queryParams.get("locatedAt");
				}
		        
				String greeting = myService.getGreeting();
				session.getAsyncRemote().sendText(greeting + name + " " + age + " y/o " + "FROM " + locatedAt + ", got your message (" + message + "). Thanks!");
			}
		}
	}
	
	class MyPongHandler implements MessageHandler.Whole<PongMessage> {

		final Session session;
		boolean isResponding = true;
		
		public MyPongHandler(Session session) {
			this.session = session;
		}
		
		@Override
		public void onMessage(PongMessage message) {
			try {
				if (isResponding)
					session.getAsyncRemote().sendPong(pongload);
			} catch (IllegalArgumentException | IOException e) {
				e.printStackTrace();
			}
		}

		public boolean isResponding() {
			return isResponding;
		}

		public void setResponding(boolean isResponding) {
			this.isResponding = isResponding;
		}

	}

	/**
	 * Returning Query Parameters as a Map.
	 * 
	 * @param query
	 * @return a map with Query Parameters
	 */
	public static Map<String, String> getQueryMap(String query) {
		Map<String, String> map = new HashMap<String, String>();
		if (query != null) {
			String[] params = query.split("&");
			for (String param : params) {
				String[] nameval = param.split("=");
				map.put(nameval[0], nameval[1]);
			}
		}
		return map;
	}
}
