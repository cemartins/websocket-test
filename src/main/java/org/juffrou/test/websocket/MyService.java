package org.juffrou.test.websocket;

import org.springframework.stereotype.Service;

/**
 * Spring bean. This bean will be automatically injected in MyEndpoint and used to compose the reply message.
 * @author cem
 *
 */
@Service
public class MyService {
	
	private static String[] greetings = new String[] {"Olá", "Howdy", "Hello", "Hola", "Hi"};

	public String getGreeting() {
		double random = Math.random() * greetings.length;
		int i = (int) random;
		return greetings[i];
	}
}
