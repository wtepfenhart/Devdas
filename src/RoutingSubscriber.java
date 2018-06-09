import java.io.IOException;
import java.util.ArrayList;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 *
 * @file RoutingSubscriber.java
 * @author wtepfenhart
 * @date: Jun 9, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */

/**
 * @author wtepfenhart
 *
 */
public class RoutingSubscriber {
	private Configuration configuration;
	private String exchange;
	private ArrayList<String> routes;

	/**
	 * 
	 */
	public RoutingSubscriber(Configuration config, String exch, ArrayList<String> rts) {
		configuration = config;
		exchange = exch;
		routes = new ArrayList<String>(rts);
		this.recieveMessage(exch);
	}

	/**
	 * 
	 * @param exch -the exchange for publish/subscribe 
	 */
	public void recieveMessage(String exch) {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(configuration.getIpAddress());
		factory.setUsername(configuration.getUserName());
		factory.setPassword(configuration.getUserPassword());
		factory.setVirtualHost(configuration.getVirtualHost());
		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.exchangeDeclare(exchange, "direct");
			String queueName = channel.queueDeclare().getQueue();
			for (String rt : routes) {
				channel.queueBind(queueName, exch, rt);
			}

			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope,
						AMQP.BasicProperties properties, byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					handleMessage(consumerTag, envelope, properties, message);
				}
			};
			channel.basicConsume(queueName, true, consumer);
		}
		catch (Exception e) {
			System.out.println(e);
			e.printStackTrace(System.out);
		}

	}
	
	public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, String msg) {
		String rk = envelope.getRoutingKey();
		System.out.println("Key: " + rk + "\tMessage: " + msg);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> rts = new ArrayList<String>();
		rts.add("Log");
		rts.add("Test");
		@SuppressWarnings("unused")
		RoutingSubscriber myReciever;
		Configuration config = new Configuration(args);
		myReciever = new RoutingSubscriber(config, "Stuff", rts);

	}

}
