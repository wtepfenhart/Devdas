/**
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
	 * @param application
	 * @param queueName
	 */
	public Recieve(Configuration application, String exch) {
		app = application;
		this.recieveMessage(exch);
	}



	/**
	 * 
	 * @param msg
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
	
	
	public void handleMessage(String message) {
        System.out.println(" [x] Received '" + message + "'");
	}
	
	/**
	 * @param argv
	 * used for debugging and testing the recieve class code
	 */
	public static void main(String[] argv) throws Exception {
		@SuppressWarnings("unused")
		Recieve myReciever;
		Configuration config = new Configuration(argv);
		myReciever = new Recieve(config, "Log");
	}

}
