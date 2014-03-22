package org.juffrou.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;

import org.glassfish.tyrus.client.ClientManager;
import org.juffrou.test.websocket.client.MyClientEndpoint;

public class WebsocketClientTest {

	public static CountDownLatch messageLatch;
    
	public static void main(String[] args) {
		
		messageLatch = new CountDownLatch(1);

		final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
		
		try {
			
			ClientManager client = ClientManager.createClient();
			
			client.connectToServer(new MyClientEndpoint(), cec, new URI("ws://localhost:8080/websocket-test/wstest"));
			ExecutorService executor = client.getExecutorService();
			messageLatch.await(100, TimeUnit.SECONDS);
			executor.shutdown();
			
		} catch (DeploymentException | IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
