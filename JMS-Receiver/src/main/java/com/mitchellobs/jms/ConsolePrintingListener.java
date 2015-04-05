package com.mitchellobs.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.StreamMessage;
import java.nio.charset.StandardCharsets;

/**
 * Prints message contents to the console.
 */
public class ConsolePrintingListener implements MessageListener {

	@Override
	public void onMessage(Message message) {
		if (message instanceof StreamMessage) {
			StreamMessage streamMessage = (StreamMessage) message;
			byte[] bytes = new byte[1024];
			int bytesRead;
			try {
				while ((bytesRead = streamMessage.readBytes(bytes)) != -1) {
					System.out.print(new String(bytes, 0, bytesRead, StandardCharsets.UTF_8));
				}
				System.out.println();
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
