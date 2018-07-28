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
 
//    @Override
//    public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,String message)
//    {
//    	System.out.println();
//    }
    
    public static void main(String[] args)
    {
        @SuppressWarnings("unused")
        CommandServiceSubscriber cmdReciever;
        Configuration config = new Configuration(args);
        cmdReciever = new CommandServiceSubscriber(config, "Testing");
    }
}