package org.juffrou.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.PongMessage;
import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebsocketClientEndpoint extends Endpoint {

	static final Logger logger = LogManager.getLogger(WebsocketClientEndpoint.class.getName());

	private Session session = null;
	private TextArea receiveTextArea;
	private MyPongHandler pongHandler;
	
	
	@Override
	public void onOpen(Session session, EndpointConfig config) {
		
		this.session = session;
		
		System.out.println("CLIENT CONNECTED");
		
		session.addMessageHandler(new MessageHandler.Whole<String>() {
			@Override
			public void onMessage(String message) {
				
				// Message received from the server - Update the textarea on the JFX thread
				Platform.runLater(new Runnable() {
			        @Override
			        public void run() {
			        	if(receiveTextArea != null)
			        		receiveTextArea.setText(message);
			        }
			   });
			}
		});

		pongHandler = new MyPongHandler(session);
		session.addMessageHandler(pongHandler);

	}
	
	@Override
	public void onError(Session session, Throwable thr) {
		super.onError(session, thr);
		this.session = null;
		logger.error("CLIENT DISCONNECTED", thr);
	}
	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		super.onClose(session, closeReason);
		this.session = null;
		logger.error("CLIENT DISCONNECTED");
	}
	
	public void setReceiveTextArea(TextArea receiveTextArea) {
		this.receiveTextArea = receiveTextArea;
	}

	public void close() {
		if(session != null) {
			
			pongHandler.stop();
			
			try {
				session.close();
			} catch (IOException e) {
				logger.error("Could not close websocket session on window close.", e);
			}
		}
	}
	
	public void sendTextToServer(String text) {
		if(session != null)
			session.getAsyncRemote().sendText(text);
	}
	
	/**
	 * Receives a PONG from the server and sends a PONG to the server
	 * @author cemartins
	 *
	 */
	private class MyPongHandler implements MessageHandler.Whole<PongMessage> {
		private final Session session;
		private final ByteBuffer pongload;
		private boolean isRunning = true;

		public MyPongHandler(Session session) {
			this.session = session;
			this.pongload = ByteBuffer.wrap("asdfghjkl".getBytes(Charset.defaultCharset()));
		}
		
		public void stop() {
			isRunning = false;
		}
		
		@Override
		public void onMessage(PongMessage message) {
			try {
				Platform.runLater(new Runnable() {
			        @Override
			        public void run() {
			        	if(receiveTextArea != null)
			        		receiveTextArea.appendText("Pong...\n");
			        }
			   });

				if(isRunning) {
					
					Thread.sleep(1000);
					
					session.getAsyncRemote().sendPong(pongload);
					
				}
				
			} catch (IllegalArgumentException | IOException | InterruptedException e) {
				logger.error("Could not send pong response.", e);
			}
		}
	}


}
