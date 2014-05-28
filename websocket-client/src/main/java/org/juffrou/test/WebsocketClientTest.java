package org.juffrou.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;

import javafx.application.Application;
import javafx.application.Application.Parameters;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.container.jdk.client.JdkClientContainer;

public class WebsocketClientTest extends Application {

    private static final String SERVER_URL="ws://localhost:8080/websocket-test/wstest";
    
    private ClientManager client = null;

    
    public static void main(String[] args) {
		launch(args);
	}

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

	private void initWebsocketClient(WebsocketClientController controller) {
		ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
		
		try {
			
			client = ClientManager.createClient(JdkClientContainer.class.getName());
			
			client.connectToServer(controller, cec, new URI(SERVER_URL));
			
		} catch (DeploymentException | IOException | URISyntaxException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void init() throws Exception {
		super.init();
		Parameters parameters = getParameters();
		if(parameters != null) {
			
			System.out.println("Parameters");
			
			for(Entry<String, String> entry : parameters.getNamed().entrySet()) {
				System.out.println("Key:   " + entry.getKey());
				System.out.println("Value: " + entry.getValue());
			}
		}
		
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

}
