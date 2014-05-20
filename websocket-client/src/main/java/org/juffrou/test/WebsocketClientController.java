package org.juffrou.test;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

public class WebsocketClientController extends Endpoint {

	@FXML
	private Button sendButton;
	
	@FXML
	private TextArea sendTextArea;
	
	@FXML
	private TextArea receiveTextArea;
	
	private Session session = null;
	

	@FXML
	private void sendButtonPressed(ActionEvent action) {
		try {
			// Send message to server
			session.getBasicRemote().sendText(sendTextArea.getText());
		} catch (IOException e) {
			e.printStackTrace();
		}
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
}
