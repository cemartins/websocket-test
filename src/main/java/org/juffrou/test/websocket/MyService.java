package org.juffrou.test.websocket;

import org.springframework.stereotype.Service;

@Service
public class MyService {
	
	private static String[] greetings = new String[] {"Olá, Howdy, Hello, Hola, Hi"};

	public String getGreeting() {
		double random = Math.random() * greetings.length;
		return greetings[(int)Math.round(random)];
	}
}
