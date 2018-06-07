import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LogRecieve extends Recieve {

	public LogRecieve(Configuration application, String exch) {
		super(application, exch);
		// TODO Auto-generated constructor stub
	}
	
	@Override
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
