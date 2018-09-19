package devdas;
/**
 *
 * @file LogSubscriber.java
 * @author wtepfenhart
 * @date: May 29, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

/**
 * 
 * @author wtepfenhart
 *
 */
public class LogSubscriber extends ExchangeSubscriber {

	/**
	 * 
	 * @param configuration - the configuration object
	 * @param exch - the exchange for rabbitmq publish/subscribe
	 */
	public LogSubscriber(Configuration configuration, String exch) {
		super(configuration, exch);
		
	}

	/**
	 * 
	 * @param msg - the json message to be handled
	 * <p/> Overrides the handleMessage for messages that
	 * are JSON strings with:
	 * <ul>
	 * 	<li> Event : Event Type identifier </li> 
	 * 	<li> Severity: Level of severity </li> 
	 *  <li> Message: Message describing event </li> 
	 *  <li> TimeStamp: Identifies when the event happened </li> 
	 *  </ul>
	 */
	@Override 
	public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,String msg) {
		JSONParser parser = new JSONParser();
		Object o = new JSONParser();
		try {
			o = parser.parse(msg);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject j= (JSONObject) o;
		if (j.get("Event").equals("quit")) { 
			System.err.println("Recieved Quit Message");
			System.exit(1);
		};
		System.err.println(j.get("Event") + "\t" + j.get("Severity") + "\t" + j.get("Message") + "\t" + j.get("TimeStamp"));
	}

	/**
	 * Used For testing purposes <i>replace with junit tests </i>
	 * 
	 * @param args - command line arguments
	 * 
	 */
	public static void main(String[] args) {
		// TODO replace with jUnit testing
		@SuppressWarnings("unused")
		ExchangeSubscriber myReciever, logReciever;
		Configuration config = new Configuration(args);
		logReciever = new LogSubscriber(config, "Log");
	}

}
