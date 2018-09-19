package devdas;
/**
 *
 * @file LogPublisher.java
 * @author wtepfenhart
 * @date: May 29, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */

import java.util.Date;

import org.json.simple.JSONObject;

/**
 * @author wtepfenhart
 *
 */
public class LogPublisher extends ExchangePublisher {

	public LogPublisher(Configuration application, String exch) {
		super(application, exch);
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param evnt - the event being logged
	 * @param msg - message describing the event
	 * @param severity - severity of event
	 */
	public void sendLogMessage(String evnt, String msg, String severity) {
		JSONObject j = new JSONObject();
		Date d = new Date();
		j.put("Event", evnt);
		j.put("Message", msg);
		j.put("Severity", severity);
		j.put("TimeStamp", "\'" + d + "\'");
		this.sendMessage(j.toJSONString());
	}

	/**
	 * 
	 * @param args - command line arguments
	 */
	public static void main(String[] args) {
		Configuration config = new Configuration(args);
		LogPublisher s = new LogPublisher(config, "Log");
		s.sendLogMessage("Start", "Started Logsend Main", "Info");
		for (int i = 0; i < 500 ; i++) {
			s.sendLogMessage("Test", "This is a test message #" + i, "Info");
			try {
				sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//s.sendLogMessage("quit", "Should quit", "High");

	}

}
