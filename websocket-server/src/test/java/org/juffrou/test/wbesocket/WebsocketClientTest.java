package org.juffrou.test.wbesocket;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.ThreadPoolConfig;
import org.glassfish.tyrus.container.jdk.client.JdkClientContainer;
import org.juffrou.test.websocket.MyApplication;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.ContextLoaderListener;

import jnlp.sample.servlet.JnlpDownloadServlet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestContextConfiguration.class})
public class WebsocketClientTest {

	private static Server jettyServer;
	private CountDownLatch messageLatch;
    private static final String SENT_MESSAGE = "Hello Testing World";
    private static final String SERVER_URL="ws://localhost:8080/websocket-test/wstest";
    private static ClientEndpointConfig cec = null;
    private static ClientManager client = null;
    private static ThreadPoolConfig workerThreadPoolConfig = null;
	
	@BeforeClass
	public static void globalSetup() throws Exception {
		System.out.println("Setup");

		setupWebsocketClient();
		setupJettyServlet();
		
	}
	
	private static void setupJettyServlet() throws Exception {
		
		// Webserver setup
		jettyServer = new Server(8080);
		
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/websocket-test");

		String currentDir = System.getProperty("user.dir");
		System.out.println("Working Directory = " + currentDir);
		
		// Setup Spring context
		context.addEventListener(new ContextLoaderListener());
		context.setBaseResource(Resource.newResource(currentDir + "/src/main/webapp"));
		context.setInitParameter("contextConfigLocation", "WEB-INF/application-context.xml");
		
		// Add servlets
		context.addServlet(new ServletHolder(new JnlpDownloadServlet()), "/webstart/*");

		jettyServer.setHandler(context);

		// Add websocket support
		context.addEventListener(new MyApplication());
		WebSocketServerContainerInitializer.configureContext(context);

		jettyServer.start();
	}
	
	private static void setupWebsocketClient() {
		// Websocket client setup
		cec = ClientEndpointConfig.Builder.create().build();
		client = ClientManager.createClient(JdkClientContainer.class.getName());
		
		workerThreadPoolConfig = ThreadPoolConfig.defaultConfig();					
		workerThreadPoolConfig.setDaemon(false);
		workerThreadPoolConfig.setMaxPoolSize(4);
		workerThreadPoolConfig.setCorePoolSize(3);

		client.getProperties().put(ClientProperties.SHARED_CONTAINER, false);
		client.getProperties().put(ClientProperties.WORKER_THREAD_POOL_CONFIG, workerThreadPoolConfig);
	}

	@AfterClass
	public static void globalShutdown() {
		System.out.println("Shutdown");
		
		if(client != null)
			client.shutdown();
		
		if(jettyServer != null)
			try {
				jettyServer.stop();
			} catch (Exception e) {
			}
	}

	 @Test
	 public void myTest() throws DeploymentException, IOException, URISyntaxException, InterruptedException {
			System.out.println("Test");

			messageLatch = new CountDownLatch(1);
			try {
				client.connectToServer(new ClientTestEndpoint(), cec, new URI(SERVER_URL));
			}
			catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		    boolean mesageReceivedByClient = messageLatch.await(30, TimeUnit.SECONDS);
		    Assert.assertTrue("Time lapsed before message was received by client.", mesageReceivedByClient);
	 }
	 
	 private class ClientTestEndpoint extends Endpoint {

		@Override
		public void onOpen(Session session, EndpointConfig config) {
	        try {
	            session.addMessageHandler(new MessageHandler.Whole<String>() {

	                @Override
	                public void onMessage(String message) {
	                    System.out.println("TEST CLIENT Received message: "+message);
	                    messageLatch.countDown(); // signal that the message was received by the client
	                }
	            });
	            session.getBasicRemote().sendText(SENT_MESSAGE);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
		 
	 }
}
