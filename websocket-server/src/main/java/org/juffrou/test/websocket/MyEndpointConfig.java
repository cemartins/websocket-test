package org.juffrou.test.websocket;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.server.ServerEndpointConfig;

/**
 * @author cem
 *
 */
public class MyEndpointConfig implements ServerEndpointConfig {

	private final String path;
	private final Class<?> endpointClass;
	private final Map<String, Object> userProperties = new HashMap<String, Object>();

	MyEndpointConfig(Class<?> endpointClass, String path) {
		this.endpointClass = endpointClass;
		this.path = path;
	}
	
	@Override
	public List<Class<? extends Encoder>> getEncoders() {
		return Collections.emptyList();
	}

	@Override
	public List<Class<? extends Decoder>> getDecoders() {
		return Collections.emptyList();
	}

	@Override
	public Map<String, Object> getUserProperties() {
		return userProperties;
	}

	@Override
	public Configurator getConfigurator() {
		return new SpringConfigurator();
	}

	@Override
	public Class<?> getEndpointClass() {
		return endpointClass;
	}

	@Override
	public List<Extension> getExtensions() {
		return Collections.emptyList();
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public List<String> getSubprotocols() {
		return Collections.emptyList();
	}

}
