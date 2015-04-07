package com.mitchellobs.web;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handles all requests by publishing them to a JMS queue.
 */
@WebServlet(name = "default", urlPatterns = {"/"})
public class DefaultServlet extends HttpServlet {

	@Resource(name = "jms/connectionFactory")
	private ConnectionFactory factory;

	@Resource(name = "jms/destination1")
	private Destination destination1;

	//@Resource(name = "jms/destination2")
	private Destination destination2;

	private Session session1;
	private MessageProducer producer1;
	private Connection connection1;
	private Connection connection2;
	private Session session2;
	private MessageProducer producer2;

	public DefaultServlet() throws NamingException {
		destination2 = (Destination) new InitialContext().lookup("java:comp/env/jms/destination2");
	}

	@PostConstruct
	public void init() {
		try {
			connection1 = factory.createConnection();
			session1 = connection1.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer1 = session1.createProducer(destination1);
			connection2 = factory.createConnection();
			session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer2 = session2.createProducer(destination2);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	@PreDestroy
	public void dispose() {
		try {
			connection1.close();
			connection2.close();
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

		String destination = req.getParameter("destination");
		Session session;
		MessageProducer producer;
		if (destination.equals("1")) {
			session = session1;
			producer = producer1;
		} else {
			session = session2;
			producer = producer2;
		}

		String messageText = req.getParameter("text");
		try {
			TextMessage message = session.createTextMessage();
			message.setText(messageText);
			producer.send(message);
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("text/plain");
		resp.getWriter().write("Hello World".toCharArray());
	}
}
