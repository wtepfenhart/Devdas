/**
 *
 * @file Send.java
 * @author wtepfenhart
 * @date: May 29, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */
import java.util.Scanner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * @author wtepfenhart
 * Made this a thread so that it can operate without blocking any other
 * functionality of the program
 *
 */
public class Send extends Thread{
	private Configuration configuration;
	private String exchange;
	private boolean running;
	private String message;


	/**
	 * 
	 * @param config - this is an object that contains the configuration data
	 * @param queueName - this is the name of the rabbit queue for publish/subscribe
	 */
	public Send(Configuration config, String exch) {
		configuration = config;
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
	 * @param running - the setter to indicated that the thread should be running
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * @param message - the message to set for sending
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	/**
	 * 
	 * @param msg - parameter for the message to be sent
	 */
	public void sendMessage(String msg) {
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
			//			System.out.println(e);
		}

	}

	/**
	 * @param argv - command line arguments
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
