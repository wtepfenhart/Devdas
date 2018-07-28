package commandservice;

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
    	CommandService commander = new CommandService(message);
    	
    	if(commander.hasCommand())
    	{
    		System.out.println(" [x] Received command: " + commander.getCommand());
    	}
    	else if(commander.hasResponse())
    	{
    		System.out.println(" [x] Received response: " + commander.getResponse());
    	}
    	else
    	{
    		System.out.println(" [x] Received no command or response");
    	}
    }
    
    public static void main(String[] args)
    {
        @SuppressWarnings("unused")
        CommandServiceSubscriber cmdReciever;
        Configuration config = new Configuration(args);
        cmdReciever = new CommandServiceSubscriber(config, "Testing");
    }
}