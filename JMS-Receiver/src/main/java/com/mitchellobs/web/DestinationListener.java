package com.mitchellobs.web;

import com.mitchellobs.jms.ConsolePrintingListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.jms.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Creates a JMS listener on servlet creation.
 */
@WebServlet(urlPatterns = { "/" })
public class DestinationListener extends HttpServlet {

	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private boolean running = true;

	Runnable listenLoop = new Runnable() {

		MessageListener listener = new ConsolePrintingListener();

		@Override
		public void run() {
			while (running) {
				try {
					Message message = consumer1.receive(1000);
					listener.onMessage(message);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
	};

	@Resource(name = "jms/connectionFactory")
	private ConnectionFactory factory;

	@Resource(name = "jms/destination1")
	private Destination destination1;
	private Connection connection;
	private MessageConsumer consumer1;

	@PostConstruct
	public void init() {
		try {
			connection = factory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			consumer1 = session.createConsumer(destination1);
//			consumer1.setMessageListener(new ConsolePrintingListener());
			connection.start();
			executor.submit(listenLoop);

		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	@PreDestroy
	public void dispose() {
		try {
			running = false;
			connection.close();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

}
