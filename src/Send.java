/**
 * 
 */
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
/**
 * @author wtepfenhart
 *
 */
public class Send {



	 private final static String QUEUE_NAME = "rabbit-server";
	  
	 
	/**
	 * @param args
	 */
	 public static void main(String[] argv) throws Exception {
		// TODO Auto-generated method stub
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("192.168.1.5");
	    factory.setUsername("rabbiter");
	    factory.setPassword("rabbiter");
	    factory.setVirtualHost("myhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();

	    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	    String message = "Hello World!";
	    channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
	    System.out.println(" [x] Sent '" + message + "'");

	    channel.close();
	    connection.close();
	  }

}
