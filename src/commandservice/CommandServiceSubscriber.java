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
	
    public CommandServiceSubscriber(Configuration config, String exch)
    {
        super(config, exch);
    }
    
	@SuppressWarnings("deprecation")
	@Override
    public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,String message)
    {	
    	System.out.println(" [x] Received '" + consumerTag + " Messsage: " + message + "'");
    	
    	this.cmd = new CommandService(message); //CommandService with no commandID
		
		// TODO Command handling
    	if(cmd.hasCommand())
    	{
    		switch(cmd.getCommand().toLowerCase())
    		{
    			case "quit":
    				System.err.println("Received Quit Message");
        			cmd.setResponse("Terminate");
        		default:
        			cmd.setResponse("Error");
        			cmd.setExplanation("Unexpected command");		
    		}
    	}
    	else
    	{
    		cmd.setResponse("Error");
    		cmd.setExplanation("No command");
    	}
    	
		System.err.println(cmd.getCommandID() + "\t" + cmd.getSource() + "\t" + cmd.getDestination() + "\t" + cmd.getCommand() + "\t" + cmd.getResponse() + "\t" + cmd.getExplanation());
    }
    
    public String getResponse()
    {
    	return cmd.getResponse();
    }
    
    public static void main(String[] args)
    {
        @SuppressWarnings("unused")
        CommandServiceSubscriber cmdReciever;
        Configuration config = new Configuration(args);
        cmdReciever = new CommandServiceSubscriber(config, "Testing");
    }
}