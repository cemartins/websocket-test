package org.juffrou.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;

import org.glassfish.tyrus.client.ClientManager;
import org.juffrou.test.websocket.client.MyClientEndpoint;

public class WebsocketClientTest extends Application {

    private static final String SERVER_URL="ws://localhost:8080/websocket-test/wstest";
    
    ClientManager client;

	@Override
	public void start(Stage primaryStage) throws Exception {
		initWebsocketClient();
		primaryStage.setTitle("Websocket Messaging Client");

		Parent root = FXMLLoader.load(getClass().getResource("WebsocketClientTest.fxml"));
		Scene scene = new Scene(root, 480, 320);
		primaryStage.setScene(scene);
		
		primaryStage.show();
	}
	
	@Override
	public void stop() throws Exception {
		ExecutorService executor = client.getExecutorService();
		executor.shutdownNow();
		super.stop();
	}

	private void initWebsocketClient() {
		ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
		
		try {
			
			client = ClientManager.createClient();
			
			client.connectToServer(new MyClientEndpoint(), cec, new URI(SERVER_URL));
			
		} catch (DeploymentException | IOException | URISyntaxException e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
