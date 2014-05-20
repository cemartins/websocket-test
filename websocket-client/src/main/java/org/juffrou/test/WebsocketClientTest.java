package org.juffrou.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;

import org.glassfish.tyrus.client.ClientManager;

public class WebsocketClientTest extends Application {

    private static final String SERVER_URL="ws://localhost:8080/websocket-test/wstest";
    
    ClientManager client = null;

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Websocket Messaging Client");

		FXMLLoader loader = new FXMLLoader(getClass().getResource("WebsocketClientTest.fxml"));
		Parent root = loader.load();
		WebsocketClientController controller = (WebsocketClientController) loader.getController();

		initWebsocketClient(controller);
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				if(client != null) {
					ExecutorService executor = client.getExecutorService();
					client = null;
					executor.shutdownNow();
				}
			}
			
		});
		
		Scene scene = new Scene(root, 480, 320);
		primaryStage.setScene(scene);
		
		primaryStage.show();
	}
	
	@Override
	public void stop() throws Exception {
		if(client != null) {
			ExecutorService executor = client.getExecutorService();
			client = null;
			executor.shutdownNow();
			super.stop();
		}
	}

	private void initWebsocketClient(WebsocketClientController controller) {
		ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
		
		try {
			
			client = ClientManager.createClient();
			
			client.connectToServer(controller, cec, new URI(SERVER_URL));
			
		} catch (DeploymentException | IOException | URISyntaxException e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
