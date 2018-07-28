package commandservice;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import devdas.Configuration;
//import devdas.ExchangePublisher; Should we import ExchangePublisher? Similar code, but different execution...
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.json.simple.JSONObject;

/**
 * @author B-T-Johnson
 */
public class CommandServicePublisher extends Thread //Should this extend ExchangePublisher? If so, should we create a generic Publisher?
{
    private Configuration configuration;
    private String exchange;
    private boolean running;
    private BlockingQueue<CommandService> queue;
    
    public CommandServicePublisher(Configuration config, String exch)
    {
        configuration = config;
        exchange = exch;
        queue = new ArrayBlockingQueue<CommandService>(1024);
    }
    
    @Override
    public void run()
    {
        System.out.println("Thread is running...");
        
        this.setRunning(true); //Should setRunning return a boolean stating its position? Or should we save that for a getter?
	
        while (running)
        {
            try
            {
                if (queue.size() > 0)
                {		    			
                    sendMessage(queue.take().toJSONString());
                }
                
                else
                {
                    sleep(10);
                }
            }
            catch (InterruptedException e)
            {
            	System.out.println("Died in sleep!");
		
                currentThread().interrupt();
                e.printStackTrace();
            }
        }
        
        System.out.println("Exiting CommandPublisher Thread");
    }
    
    /**
     * @param running the setter to indicated that the thread should be running
     */
    public void setRunning(boolean running)
    {
    	this.running = running;
    }
    
    /**
     * @param msg the message to set for sending
     */
    public void setMessage(JSONObject msg)
    {
    	CommandService cmd = new CommandService();
    	cmd.read(msg);
    	
    	try
        {
            queue.put(cmd);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * @param jsonMsg the message to set for sending
     */
    public void setMessage(String jsonMsg)
    {
    	CommandService cmd = new CommandService();
    	cmd.read(jsonMsg);
    	
        try
        {
            queue.put(cmd);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * @param cmd the message object to set for sending
     */
    public void setMessage(CommandService cmd)
    {
    	try
    	{
    		queue.put(cmd);
    	}
    	catch(InterruptedException e)
    	{
    		e.printStackTrace();
    	}
    }
    
    /**
     * @param msg - parameter for the message to be sent
     */
    public void sendMessage(String msg)
    {
    	// TODO Change to returning boolean to indicate success (junit testing)
    	ConnectionFactory factory = new ConnectionFactory();
    	factory.setHost(configuration.getIpAddress());
    	factory.setUsername(configuration.getUserName());
    	factory.setPassword(configuration.getUserPassword());
    	factory.setVirtualHost(configuration.getVirtualHost());
	
        try
        {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(exchange, "fanout");
            channel.basicPublish(exchange, "", null, msg.getBytes("UTF-8"));
            System.out.println(" [" + exchange + "] Sent '" + msg + "'");

            channel.close();
            connection.close();
        }
        catch (Exception e)
        {
            // TODO Add error logging here. 
            System.out.println(e);
        }
    }
    
    /**
     * @param args command line arguments
     * 
     * Used for debugging and testing the send class code
     */
    // TODO replace with junit testing
    public static void main(String[] args) throws InterruptedException
    {
        CommandServicePublisher cmdSender;
        Configuration config = new Configuration(args);
        cmdSender = new CommandServicePublisher(config, "Testing");
        
        //Send first command (as JSONObject)
        cmdSender.start();
        JSONObject first = new JSONObject();
        	first.put("Command", "First");
        cmdSender.setMessage(first);

        //Change exchange
        cmdSender = new CommandServicePublisher(config, "Intention");
        cmdSender.start();
        
        //Send second message (as JSON String)
        JSONObject speak = new JSONObject();
        	speak.put("Command", "Speak");
        cmdSender.setMessage(speak.toJSONString());
        	speak.put("Explanation", "Second message to send");
        cmdSender.setMessage(speak.toJSONString());
	
        //Allow user interaction
        Scanner scanner = new Scanner(System.in);
        System.out.println("Awaiting new messages from user: ");
            String msg = "\"Command\":\"" + scanner.nextLine() + "\"";
        cmdSender.setMessage(msg);
            msg = "\"Response\":\"" + scanner.nextLine() + "\"";
        cmdSender.setMessage(msg);
        
        //Terminate
        sleep(10);
        cmdSender.setRunning(false);
        scanner.close();
    }
}