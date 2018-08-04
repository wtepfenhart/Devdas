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
	private CommandService cmd;
	private String exchange;
	
    public CommandServiceSubscriber(Configuration config, String exch)
    {
        super(config, exch);
        exchange = exch;
    }
    
	@Override
    public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,String message)
    {	
    	System.out.println(" [x] Received '" + consumerTag + " Messsage: " + message + "'");
    	
    	this.cmd = new CommandService(message);
		
		// TODO Command handling
    	if(!cmd.hasResponse() && cmd.getDestination().equalsIgnoreCase(exchange))
    	{
    		cmd.setDestination(cmd.getSource());
    		cmd.setSource(exchange);
    		
    		if(cmd.hasCommand())
    		{
    			switch(cmd.getCommand().toLowerCase())
    			{
    				case "quit":
    					System.err.println("Received Quit Message");
    					cmd.setResponse("Terminate");
    					break;
    				default:
    					cmd.setResponse("Error");
    					cmd.setExplanation("Unexpected command");
    					break;
    			}
    		}
    		else
        	{
        		cmd.setResponse("Error");
        		cmd.setExplanation("No command");
        	}
    	}
    	else if(cmd.hasResponse() && cmd.getSource().equalsIgnoreCase(exchange))
    	{
    		cmd.setDestination(cmd.getSource());
    		cmd.setSource(exchange);
    		
    		switch(cmd.getResponse().toLowerCase())
    		{
    			case "terminate":
    				System.err.println("***QUIT***");
    				System.exit(1);
    				break;
    			case "error":
    				System.err.println("***ERROR***");
    				break;
    			default:
    				System.err.println("***NOTHING***");
    				break;
    		}
    	}
    	
		//System.err.println(cmd);
    }
	
	public CommandService getCommand()
	{
		return cmd;
	}
    
    public static void main(String[] args)
    {
        @SuppressWarnings("unused")
        CommandServiceSubscriber cmdReciever;
        Configuration config = new Configuration(args);
        cmdReciever = new CommandServiceSubscriber(config, "Testing");
    }
}