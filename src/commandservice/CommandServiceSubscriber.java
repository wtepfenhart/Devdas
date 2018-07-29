package commandservice;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import devdas.Configuration;
import devdas.ExchangeSubscriber;

/**
 * @author B-T-Johnson
 */
public class CommandServiceSubscriber extends ExchangeSubscriber //Are any methods expected to change?
{   
    public CommandServiceSubscriber(Configuration config, String exch)
    {
        super(config, exch);
    }
 
    @Override
    public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,String message)
    {
    	JSONParser parser = new JSONParser();
		Object o = new JSONParser();
		
		try
		{
			o = parser.parse(message);
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject j = (JSONObject) o;
		
		// TODO Command handling
		if (((String) j.get("Command")).equalsIgnoreCase("quit"))
		{ 
			System.err.println("Recieved Quit Message");
			System.exit(1);
		}
		
		System.err.println(j.get("Source") + "\t" + j.get("Destination") + "\t" + j.get("Command") + "\t" + j.get("Response") + "\t" + j.get("Explanation"));
    }
    
    public static void main(String[] args)
    {
        @SuppressWarnings("unused")
        CommandServiceSubscriber cmdReciever;
        Configuration config = new Configuration(args);
        cmdReciever = new CommandServiceSubscriber(config, "Testing");
    }
}