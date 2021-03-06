package org.juffrou.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.ThreadPoolConfig;
import org.glassfish.tyrus.container.jdk.client.JdkClientContainer;

public class WebsocketClientTest extends Application {
	
	static final Logger logger = LogManager.getLogger(WebsocketClientTest.class.getName());

    private static final String SERVER_URL_PARAMETER="server-url";
    private static final String SERVER_URL="ws://localhost:8080/websocket-test/wstest";
    
    public static void main(String[] args) {
    	logger.debug("Reached main");
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
    	logger.debug("Reached start");
		primaryStage.setTitle("Websocket Messaging Client");

		FXMLLoader loader = new FXMLLoader(getClass().getResource("WebsocketClientTest.fxml"));
		Parent root = loader.load();
		WebsocketClientController controller = (WebsocketClientController) loader.getController();

		Scene scene = new Scene(root, 640, 460);
		primaryStage.setScene(scene);
		
		controller.init(getServerEndpointAddress());
		
		primaryStage.show();
	}
	
	private URI getServerEndpointAddress() {
		String serverEndpointStr = SERVER_URL;
		Parameters parameters = getParameters();
		if(parameters != null && parameters.getNamed().get(SERVER_URL_PARAMETER) != null) {
			serverEndpointStr = parameters.getNamed().get(SERVER_URL_PARAMETER);
			if(serverEndpointStr.startsWith("https"))
				serverEndpointStr = serverEndpointStr.replaceFirst("https", "wss");
			else
				serverEndpointStr = serverEndpointStr.replaceFirst("http", "ws");
			serverEndpointStr = serverEndpointStr + "/wstest";
			logger.debug("Calculated Server Endpoint Address: " + serverEndpointStr);
		}
		return URI.create(serverEndpointStr);
	}

	
	@SuppressWarnings("restriction")
	@Override
	public void init() throws Exception {
		super.init();
		logger.debug("Reached init");
		Parameters parameters = getParameters();
		if(parameters != null) {
			
			logger.debug("Parameters:");
			
			Map<String, String> named = parameters.getNamed();
			if(named != null) {
				logger.debug("  Named: " + named.size());
				for(Entry<String, String> entry : named.entrySet()) {
					logger.debug("    " + entry.getKey() + "= " + entry.getValue());
				}
			}
			else
				logger.debug("  Named: NULL");
			
			List<String> unnamed = parameters.getUnnamed();
			if(unnamed != null) {
				logger.debug("  unnamed: " + unnamed.size());
				for(String param : unnamed) {
					logger.debug("    " + param);
				}
			}
			else
				logger.debug("  unnamed: NULL");
			
			List<String> raw = parameters.getRaw();
			if(raw != null) {
				logger.debug("  raw: " + raw.size());
				for(String param : raw) {
					logger.debug("    " + param);
				}
			}
			else
				logger.debug("  raw: NULL");
		}
		
	}

	@Override
	public void stop() throws Exception {
		logger.debug("Reached stop");
		super.stop();
	}

}
