import java.util.Date;

import org.json.simple.JSONObject;

public class LogSend extends Send {

	public LogSend(Configuration application, String exch) {
		super(application, exch);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public void sendLogMessage(String evnt, String msg, String severity) {
		JSONObject j = new JSONObject();
		Date d = new Date();
		j.put("Event", evnt);
		j.put("Message", msg);
		j.put("Severity", severity);
		j.put("TimeStamp", "\'" + d + "\'");
		this.sendMessage(j.toJSONString());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Configuration config = new Configuration(args);
		LogSend s = new LogSend(config, "Log");
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
