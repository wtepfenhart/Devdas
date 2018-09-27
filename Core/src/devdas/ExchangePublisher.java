/**
 *
 * @file ExchangePublisher.java
 * @author wtepfenhart
 * @date: May 29, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */

package devdas;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * @author wtepfenhart
 *  <p/>
 * The EchangePublisher class publishes a message (Java String message)
 * in a rabbitmq exchange. It can be wrapped or extended to allow
 * controlled construction of formatted messages such as JSON, XML, CSV, or
 * whatever.
 * 
 * <p/>
 * It opens and closes the connection to rabbitmq for each message that it needs to 
 * send. This is necessary since leaving the connection open will timeout with 
 * the result that attempts to send a message will fail when
 * there are large time delays between messages.
 * 
 * <p/>
 * Made this a thread so that it can operate without blocking any other
 * functionality of the program. 
 * 
 */
/* This is the publisher for the SystemCommands */
public class ExchangePublisher extends Thread{
	private Configuration configuration;
	private String exchange;
	private boolean running;
	private BlockingQueue<String> queue;

	/**
	 * 
	 * @param config - this is an object that contains the configuration data
	 * @param queueName - this is the name of the rabbit queue for publish/subscribe
	 */
	public ExchangePublisher(Configuration config, String exch) {
		configuration = config;
		exchange = exch;
		queue = new ArrayBlockingQueue<String>(1024);
	}

	/**
	 *
	 */
	public void run(){  
//		System.out.println("thread is running..."); 
		running = true;
		while (running) {
			try {
				if (queue.size() > 0) {		    			
					sendMessage(queue.take());
				}
				else {
					sleep(10);
				}
			} catch (InterruptedException e) {
				System.out.println("Died in sleep!");
				// TODO Auto-generated catch block
				currentThread().interrupt();
				e.printStackTrace();
			}
		}
		System.out.println("Exiting ExchangePublisher Thread");
	}


	/**
	 * @param running - the setter to indicated that the thread should be running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * @param message - the message to set for sending
	 */
	public void setMessage(String message) {
		try {
			queue.put(message);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * @param msg - parameter for the message to be sent
	 */
	public void sendMessage(String msg) {
		// TODO Change to returning boolean to indicate success (junit testing)
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(configuration.getIpAddress());
		factory.setUsername(configuration.getUserName());
		factory.setPassword(configuration.getUserPassword());
		factory.setVirtualHost(configuration.getVirtualHost());
		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.exchangeDeclare(exchange, "fanout");
			channel.basicPublish(exchange, "", null, msg.getBytes("UTF-8"));
			//			System.out.println(" [x] Sent '" + msg + "'");

			channel.close();
			connection.close();
		}
		catch (Exception e) {
			// TODO Add error logging here. 
			//			System.out.println(e);
		}

	}

	/**
	 * @param argv - command line arguments
	 * used for debugging and testing the send class code
	 * 
	 */
	// TODO replace with junit testing
	public static void main(String[] argv) throws Exception {
		ExchangePublisher mySender;
		Configuration config = new Configuration(argv);
		mySender = new ExchangePublisher(config, "Control");
		mySender.start();
		mySender.setMessage("First");

		mySender = new ExchangePublisher(config, "Intention");
		mySender.start();
		mySender.setMessage("Speak");
		
		mySender.setMessage("Second message to send");
		Scanner scanner = new Scanner(System.in);
		String msg = scanner.nextLine();
		mySender.setMessage(msg); 
		msg = scanner.nextLine();
		mySender.setMessage(msg);
		sleep(10);
		mySender.setRunning(false);
		scanner.close();
		
	}

}
