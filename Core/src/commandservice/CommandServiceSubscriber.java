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

    	if (msg.isResponse())
    	{
    		//TODO Response handling
    		switch (msg.getParam("Response"))
    		{
				case "Failure":
					System.out.println("***FAILURE***");
					System.out.println("Could not execute command " + msg.getParam("CommandID") + " because '" + msg.getParam("Explanation") + "'");
					break;
				case "Success":
					System.out.println("***SUCCESS***");
					System.out.println(msg.getParam("Explanation"));
					break;
				default:
					System.out.println("Unknown Response"); //Should we throw an error here instead? Or just ignore this case?
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