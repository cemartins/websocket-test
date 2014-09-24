package org.juffrou.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.PongMessage;
import javax.websocket.Session;

public class WebsocketClientController extends Endpoint {
	
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
		System.out.println("CLIENT DISCONNECTED");
		thr.printStackTrace();
	}
	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		super.onClose(session, closeReason);
		System.out.println("CLIENT DISCONNECTED");
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
				
			} catch (IllegalArgumentException | IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
