package devdas;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.json.simple.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 *
 * @file RoutingPublisher.java
 * @author wtepfenhart
 * @date: Jun 8, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */

/**
 * @author wtepfenhart
 *
 */
public class RoutingPublisher extends Thread{
	private Configuration configuration;
	private String exchange;
	private boolean running;
	private BlockingQueue<RoutedMessage> queue;
	
	private class RoutedMessage {
		private String route;
		private String message;
		
		public	RoutedMessage(String r, String m) {
			route=r;
			message = m;
		}

		/**
		 * @return the route
		 */
		public String getRoute() {
			return route;
		}

		/**
		 * @param route the route to set
		 */
		public void setRoute(String route) {
			this.route = route;
		}

		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * @param message the message to set
		 */
		public void setMessage(String message) {
			this.message = message;
		}
		
	
	}

	/**
	 * 
	 */
	public RoutingPublisher(Configuration config, String exch) {
		configuration = config;
		exchange = exch;
		queue = new ArrayBlockingQueue<RoutedMessage>(1024);
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
//				System.out.println("Died in sleep!");
				currentThread().interrupt();
				e.printStackTrace();
			}
		}
//		System.out.println("Exiting ExchangePublisher Thread");
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
	public void setMessage(String route, String message) {
		try {
			RoutedMessage rm = new RoutedMessage(route,message);
			queue.put(rm);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

	/**
	 * 
	 * @param msg - parameter for the message to be sent
	 */
	public void sendMessage(RoutedMessage msg) {
		// TODO Change to returning boolean to indicate success (junit testing)
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(configuration.getIpAddress());
		factory.setUsername(configuration.getUserName());
		factory.setPassword(configuration.getUserPassword());
		factory.setVirtualHost(configuration.getVirtualHost());
		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.exchangeDeclare(exchange, "direct");
			channel.basicPublish(exchange, msg.getRoute(), null, msg.getMessage().getBytes("UTF-8"));
//			System.out.println(" [x] Sent " + msg.getRoute() + " Message: " + msg.getMessage());

			channel.close();
			connection.close();
		}
		catch (Exception e) {
			// TODO Add error logging here.
			//			System.out.println(e);
		}

	}

	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		JSONObject a = new JSONObject();
		a.put("Command", "Test");
		RoutingPublisher mySender;
		Configuration config = new Configuration(args);
		mySender = new RoutingPublisher(config, "Agent");
		mySender.start();
		mySender.setMessage("All",a.toString());
		a.put("Say", "Testing more");
		mySender.setMessage("Command", a.toString());
		Scanner scanner = new Scanner(System.in);
		String msg = scanner.nextLine();
		a.put("Say", msg);
		mySender.setMessage("Command",a.toString());
		msg = scanner.nextLine();
		a.put("Say", msg);
		mySender.setMessage("Command",a.toString());
		try {
			sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mySender.setRunning(false);
		scanner.close();
		// TODO Auto-generated method stub
		
	}

}
