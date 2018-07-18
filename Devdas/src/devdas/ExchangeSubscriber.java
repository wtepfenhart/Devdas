package devdas;
/**
 *
 * @file ExchangeSubscriber.java
 * @author wtepfenhart
 * @date: May 29, 2018
 * Copyright wtepfenhart (c) 2018
 * 
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


/**
 * @author wtepfenhart
 * 
 * <p/>
 * This class to be a basic subscriber that is
 * to be extended by overriding the handleMesssage(msg) method.
 * The handleDelivery is a pass-through method defined
 * within an nested autonomous class.
 * 
 *
 */
public class ExchangeSubscriber{
	private Configuration configuration;
	private String exchange;


	/**
	 * 
	 * @param configuration - configuration object
	 * @param exchange - the rabbitmq exchange for publish/subscribe
	 */
	public ExchangeSubscriber(Configuration config, String exch) {
		configuration = config;
		exchange = exch;
		this.recieveMessage(exchange);
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

			channel.exchangeDeclare(exch, "fanout");
			String queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, exch, "");

			System.out.println(" [" + exch  + "] " + "\tWaiting for messages.");

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
		}

	}

	/**
	 * 
	 * @param message - the message received from rabbitmq
	 * <p/>
	 * This method is intended to be overridden for different kinds of messages
	 */
	public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,String message) {
		System.out.println(" [x] Received '" + consumerTag + " Messsage: " + message + "'");
	}

	/**
	 * @param argv - command line arguments
	 * used for debugging and testing the receive class code
	 */
	public static void main(String[] argv) throws Exception {
		@SuppressWarnings("unused")
		ExchangeSubscriber myReciever;
		Configuration config = new Configuration(argv);
		myReciever = new ExchangeSubscriber(config, "Testing");
	}

}
