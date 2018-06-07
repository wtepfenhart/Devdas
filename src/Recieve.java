/**
 *
 * @file Recieve.java
 * @author wtepfenhart
 * @date: May 29, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */
import java.io.IOException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

// TODO Move connection details from DevDasMain to config object

/**
 * @author wtepfenhart
 *
 */
public class Recieve{
	private Configuration app;


	/**
	 * 
	 * @param configuration - configuration object
	 * @param exchange - the rabbitmq exchange for publish/subscribe
	 */
	public Recieve(Configuration configuration, String exchage) {
		app = configuration;
		this.recieveMessage(exchage);
	}



	/**
	 * 
	 * @param exch -the exchange for publish/subsribe 
	 */
	public void recieveMessage(String exch) {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(app.getIpAddress());
		factory.setUsername(app.getUserName());
		factory.setPassword(app.getUserPassword());
		factory.setVirtualHost(app.getVirtualHost());
		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.exchangeDeclare(exch, "fanout");
			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, exch, "");

			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope,
						AMQP.BasicProperties properties, byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					handleMessage(message);
				}
			};
			channel.basicConsume(queueName, true, consumer);
		}
		catch (Exception e) {
			System.out.println(e);
		}

	}

	/**
	 * 
	 * @param message - the message received from rabbitmq
	 * this method is intended to be overriddn for different kinds of messages
	 */
	public void handleMessage(String message) {
		System.out.println(" [x] Received '" + message + "'");
	}

	/**
	 * @param argv - command line arguements
	 * used for debugging and testing the recieve class code
	 */
	public static void main(String[] argv) throws Exception {
		@SuppressWarnings("unused")
		Recieve myReciever;
		Configuration config = new Configuration(argv);
		myReciever = new Recieve(config, "Log");
	}

}
