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
	private CommandServiceMessage msg;
	private CommandServiceMessage tempMsg;
	
    public CommandServiceSubscriber(Configuration config, String exch)
    {
        super(config, exch);
    }
    
	@Override
    public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, String message)
    {	
    	super.handleMessage(consumerTag, envelope, properties, message);
    	
    	this.msg = new CommandServiceMessage(message);
    	this.tempMsg = msg;

    	if (msg.hasResponse() && msg.getDestination().equalsIgnoreCase(this.toString()))
    	{
    		//TODO Response handling
    		switch (msg.getResponse())
    		{
				case "Failure":
					System.err.println("***FAILURE***");
					System.err.println("Could not execute command " + msg.getCommandID() + " because '" + msg.getExplanation() + "'");
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
	 * Retrieves a temporary instance of the message from the subscriber, then destroys the instance afterwards
	 * 
	 * @return Returns the message as a CommandService object
	 */
	public CommandServiceMessage consumeMessage()
	{
		CommandServiceMessage temp = tempMsg;
		tempMsg = null;
		return temp;
	}
	
	public CommandServiceMessage getMessage()
	{
		return msg;
	}
    
    public static void main(String[] args)
    {
        @SuppressWarnings("unused")
        CommandServiceSubscriber cmdReciever;
        Configuration config = new Configuration(args);
        cmdReciever = new CommandServiceSubscriber(config, "Testing");
    }
}