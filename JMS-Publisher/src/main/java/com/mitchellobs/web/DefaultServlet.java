package com.mitchellobs.web;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.jms.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Handles all requests by publishing them to a JMS queue.
 */
@WebServlet(name = "default", urlPatterns = {"/"})
public class DefaultServlet extends HttpServlet {

	@Resource(name = "jms/connectionFactory")
	private ConnectionFactory factory;

	@Resource(name = "jms/destination1")
	private Destination destination1;

	private Session session;
	private MessageProducer destination1Producer;
	private Connection connection;

	@PostConstruct
	public void init() {
		try {
			connection = factory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination1Producer = session.createProducer(destination1);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	@PreDestroy
	public void dispose() {
		try {
			connection.close();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try (InputStream bodyStream = req.getInputStream()) {
			StreamMessage message = session.createStreamMessage();
			int bytesRead;
			byte[] bytes = new byte[1024];

			while ((bytesRead = bodyStream.read(bytes)) != -1) {
				message.writeBytes(bytes, 0 ,bytesRead);
			}

			destination1Producer.send(message);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("text/plain");
		resp.getWriter().write("Hello World".toCharArray());
	}
}
