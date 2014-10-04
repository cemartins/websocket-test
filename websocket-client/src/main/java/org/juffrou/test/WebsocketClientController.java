package org.juffrou.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.PongMessage;
import javax.websocket.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebsocketClientController extends Endpoint {

	static final Logger logger = LogManager.getLogger(WebsocketClientController.class.getName());

	@FXML
	private Parent root;		// allows the controller to get it's corresponding stage

	@FXML
	private Button sendButton;
	
	@FXML
	private Button startPongButton;
	
	@FXML
	private Button stopPongButton;
	
	@FXML
	private TextArea sendTextArea;
	
	@FXML
	private TextArea receiveTextArea;
	
	private Session session = null;
	

	@SuppressWarnings("restriction")
	@FXML
	private void sendButtonPressed(ActionEvent action) {
		// Send message to server
		String text = sendTextArea.getText();
		session.getAsyncRemote().sendText(text);
	}
	
	@SuppressWarnings("restriction")
	@FXML
	private void startPongButtonPressed(ActionEvent action) {
		session.getAsyncRemote().sendText("STARTPONGING");
	}
	
	@SuppressWarnings("restriction")
	@FXML
	private void stopPongButtonPressed(ActionEvent action) {
		session.getAsyncRemote().sendText("STOPPONGING");
		receiveTextArea.clear();
	}

	
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
						receiveTextArea.setText(message);
			        }
			   });
			}
		});

		session.addMessageHandler(new MyPongHandler(session));

	}
	
	@Override
	public void onError(Session session, Throwable thr) {
		super.onError(session, thr);
		logger.error("CLIENT DISCONNECTED", thr);
	}
	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		super.onClose(session, closeReason);
		logger.error("CLIENT DISCONNECTED");
	}
	
	@FXML
	private void init() {
		logger.debug("Reached init");
		Stage stage = getStage();
		EventHandler<WindowEvent> eh = new MyCloseHandler(stage);
		stage.setOnCloseRequest(eh);
	}
	
	/**
	 * Obtain this controller's stage
	 * @return the controller stage
	 */
	protected Stage getStage() {
		return (Stage) root.getScene().getWindow();
	}
	
	/**
	 * Receives a PONG from the server and sends a PONG to the server
	 * @author cemartins
	 *
	 */
	private class MyPongHandler implements MessageHandler.Whole<PongMessage> {
		private final Session session;
		private final ByteBuffer pongload;

		public MyPongHandler(Session session) {
			this.session = session;
			this.pongload = ByteBuffer.wrap("asdfghjkl".getBytes(Charset.defaultCharset()));
		}
		
		@Override
		public void onMessage(PongMessage message) {
			try {
				Platform.runLater(new Runnable() {
			        @Override
			        public void run() {
						receiveTextArea.appendText("Pong...\n");
			        }
			   });

				Thread.sleep(1000);
				
				session.getAsyncRemote().sendPong(pongload);
				
			} catch (IllegalArgumentException | IOException | InterruptedException e) {
				logger.error("Could not send pong response.", e);
			}
		}
	}
	
	private class MyCloseHandler implements EventHandler<WindowEvent> {
		
		private final Stage stage;
		
		public MyCloseHandler(Stage stage) {
			this.stage = stage;
		}

		@Override
		public void handle(WindowEvent event) {
			logger.debug("Window is closing");
			event.consume();
			if(session != null) {
				try {
					session.close();
					session = null;
				} catch (IOException e) {
					logger.error("Could not close websocket session on window close.", e);
				}
			}
			stage.close();
		}
		
	}
}
