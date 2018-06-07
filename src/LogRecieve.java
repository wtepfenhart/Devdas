/**
 *
 * @file LogRecieve.java
 * @author wtepfenhart
 * @date: May 29, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author wtepfenhart
 *
 */
public class LogRecieve extends Recieve {

	/**
	 * 
	 * @param configuration - the configuration object
	 * @param exch - the exchange for rabbitmq publish/subscribe
	 */
	public LogRecieve(Configuration configuration, String exch) {
		super(configuration, exch);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	/**
	 * @param msg - the json message to be handled
	 */
	public void handleMessage(String msg) {
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
	 * Used For testing purposes
	 * 
	 * @param args - command line arguments
	 * 
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Recieve myReciever, logReciever;
		Configuration config = new Configuration(args);
		logReciever = new LogRecieve(config, "Log");
	}

}
