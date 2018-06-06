/**
 * 
 */
import java.util.Scanner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

	// TODO Move connection details from DevDasMain to config object

/**
 * @author wtepfenhart
 *
 */
public class Send extends Thread{
	private Configuration app;
	private String exchange;
	private boolean running;
	private String message;

	
	/**
	 * 
	 * @param application
	 * @param queueName
	 */
	public Send(Configuration application, String exch) {
		app = application;
		exchange = exch;
	}

	/**
	 *
	 */
	public void run(){  
		System.out.println("thread is running..."); 
		running = true;
		while (running) {
			try {
				if (message != null) {		    			
					sendMessage(message);
					message = null;
				}
				else
					sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Exiting Send Thread");
	}

	
	/**
	 * @param running the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	
	/**
	 * 
	 * @param msg
	 */
	public void sendMessage(String msg) {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(app.getIpAddress());
		factory.setUsername(app.getUserName());
		factory.setPassword(app.getUserPassword());
		factory.setVirtualHost(app.getVirtualHost());
		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.exchangeDeclare(exchange, "fanout");
			channel.basicPublish(exchange, "", null, msg.getBytes("UTF-8"));
			System.out.println(" [x] Sent '" + msg + "'");

			channel.close();
			connection.close();
		}
		catch (Exception e) {
			System.out.println(e);
		}

	}
	
	/**
	 * @param argv
	 * used for debugging and testing the send class code
	 */
	public static void main(String[] argv) throws Exception {
		// TODO Auto-generated method stub
		Send mySender;
		Configuration config = new Configuration(argv);
		mySender = new Send(config, "Testing");
		mySender.start();
		mySender.sendMessage("Hello This is going to fail!");
		mySender.sendMessage("Second message to send");
		Scanner scanner = new Scanner(System.in);
		String msg = scanner.nextLine();
		mySender.sendMessage(msg);
		mySender.setRunning(false);
		scanner.close();
	}

}
