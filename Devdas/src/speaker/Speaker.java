/**
 *
 * @file Speaker.java
 * @author wtepfenhart
 * @date: Jun 9, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */
package speaker;

import java.util.ArrayList;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

import devdas.Configuration;
import devdas.ExchangePublisher;
import devdas.ExchangeSubscriber;
import devdas.LogPublisher;
import devdas.RoutingSubscriber;

/**
 * @author wtepfenhart
 *
 */
public class Speaker extends RoutingSubscriber implements DevdasControl{
	LogPublisher logger;
	ControlSubscriber control;
	
	public Speaker(Configuration config, String exch, ArrayList<String> rts) {
		super(config, exch, rts);
		logger = new LogPublisher(config, "Log");
		logger.sendLogMessage("Startup", "Pretend Speaker", "Info");
		control = new ControlSubscriber(config,"Control",this);
		
	}
	
	@Override
	public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, String msg) {
		String rk = envelope.getRoutingKey();
		System.out.println("Key: " + rk + "\tSay: " + msg);
	}

	public void processCommand(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,String message) {
		switch (message) {
		case "Quit":
		case "quit":
			logger.sendLogMessage("Shutdown", "Pretend Speaker", "Info");
			System.exit(1);
			break;
		default:
			logger.sendLogMessage("Default", "Pretend Speaker", "Info");
			break;
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unused")
		Speaker mySpeaker;
		ArrayList<String> tags = new ArrayList<String>();
		tags.add("Speak");
		Configuration config = new Configuration(args);
		mySpeaker = new Speaker(config, "Intention",tags);
	}

}
