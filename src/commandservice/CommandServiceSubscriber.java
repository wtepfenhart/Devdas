package commandservice;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import devdas.Configuration;
import devdas.ExchangeSubscriber;

/**
 * @author B-T-Johnson
 */
public class CommandServiceSubscriber extends ExchangeSubscriber
{
	private CommandService msg;
	private String exchange;
	
    public CommandServiceSubscriber(Configuration config, String exch)
    {
        super(config, exch);
        this.exchange = exch;
    }
    
	@Override
    public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, String message)
    {	
    	super.handleMessage(consumerTag, envelope, properties, message);
    	
    	this.msg = new CommandService(message);

    	if (msg.hasResponse() && msg.getDestination().equalsIgnoreCase(exchange))
    	{
    		//TODO Response handling
    		switch (msg.getResponse())
    		{
				case "Error":
					System.err.println("***FAILURE***");
					System.err.println("Could not execute command " + msg.getCommandID() + " because '" + msg.getExplanation());
					break;
				case "Success":
					System.err.println("***SUCCESS***");
					break;
				default:
					System.err.println("Unknown Response");
					break;
    		}
    	}
    }
	
	/**
	 * Retrieves the message from the subscriber, then destroys the message afterwards
	 * 
	 * @return Returns the message as a CommandService object
	 */
	public CommandService getMessage()
	{
		CommandService temp = msg;
		msg = null;
		return temp;
	}
    
    public static void main(String[] args)
    {
        @SuppressWarnings("unused")
        CommandServiceSubscriber cmdReciever;
        Configuration config = new Configuration(args);
        cmdReciever = new CommandServiceSubscriber(config, "Testing");
    }
}