package org.juffrou.test;

import java.io.IOException;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.ThreadPoolConfig;
import org.glassfish.tyrus.container.jdk.client.JdkClientContainer;

public class WebsocketClientController {

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
	
	private ClientManager client;
	private WebsocketClientEndpoint websocketClientEndpoint;
	

	@SuppressWarnings("restriction")
	@FXML
	private void sendButtonPressed(ActionEvent action) {
		// Send message to server
		String text = sendTextArea.getText();
		websocketClientEndpoint.sendTextToServer(text);
	}
	
	@SuppressWarnings("restriction")
	@FXML
	private void startPongButtonPressed(ActionEvent action) {
		websocketClientEndpoint.sendTextToServer("STARTPONGING");
	}
	
	@SuppressWarnings("restriction")
	@FXML
	private void stopPongButtonPressed(ActionEvent action) {
		websocketClientEndpoint.sendTextToServer("STOPPONGING");
		receiveTextArea.clear();
	}

	
	
	public void init(URI serverEndpointAddress) {
		logger.debug("Reached controller initialize");
		
		initWebsocketClient(serverEndpointAddress);
		Stage stage = getStage();
		EventHandler<WindowEvent> eh = new MyCloseHandler(stage);
		stage.setOnCloseRequest(eh);
		
	}

	private WebsocketClientEndpoint initWebsocketClient(URI serverEndpointAddress) {
    	logger.debug("Reached initWebsocketClient");
    	
    	websocketClientEndpoint = new WebsocketClientEndpoint();
		websocketClientEndpoint.setReceiveTextArea(receiveTextArea);
		
		try {
			
//			System.getProperties().put("javax.net.debug", "all");  // uncomment this line if you want tyrus debug to be output to the console
			
			client = AccessController.doPrivileged(new PrivilegedExceptionAction<ClientManager>() {

				@Override
				public ClientManager run() throws Exception {
					
					ThreadPoolConfig workerThreadPoolConfig = ThreadPoolConfig.defaultConfig();					
					workerThreadPoolConfig.setInitialClassLoader(this.getClass().getClassLoader());
					workerThreadPoolConfig.setDaemon(false);
					workerThreadPoolConfig.setMaxPoolSize(4);
					workerThreadPoolConfig.setCorePoolSize(3);
					
					ClientManager cm;
					cm = ClientManager.createClient(JdkClientContainer.class.getName());
					cm.getProperties().put(ClientProperties.SHARED_CONTAINER, false);
					cm.getProperties().put(ClientProperties.WORKER_THREAD_POOL_CONFIG, workerThreadPoolConfig);

					cm.asyncConnectToServer(websocketClientEndpoint, serverEndpointAddress);
					return cm;
				}
				
			});
						
		} catch (PrivilegedActionException e) {
	    	logger.error("Security Error establishing client connection", e);
			e.printStackTrace();
		} catch (Exception e) {
	    	logger.error("Error establishing client connection", e);
			e.printStackTrace();
		}
		
		return websocketClientEndpoint;
	}

	
	/**
	 * Obtain this controller's stage
	 * @return the controller stage
	 */
	protected Stage getStage() {
		return (Stage) root.getScene().getWindow();
	}
	
	
	private class MyCloseHandler implements EventHandler<WindowEvent> {
		
		private final Stage stage;
		
		public MyCloseHandler(Stage stage) {
			this.stage = stage;
		}

		@SuppressWarnings("restriction")
		@Override
		public void handle(WindowEvent event) {
			EventType<WindowEvent> eventType = event.getEventType();
			logger.debug("Window event: " + eventType.getName());
			websocketClientEndpoint.setReceiveTextArea(null);
			websocketClientEndpoint.close();
			try {
				Thread.sleep(500);  // wait for websocket to close
			} catch (InterruptedException e) {
				logger.error("cant sleep", e);
			}
			client.shutdown();
			Platform.exit();
			System.exit(0);
		}
		
	}
}
