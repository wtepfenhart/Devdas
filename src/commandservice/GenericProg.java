package commandservice;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import devdas.Configuration;

/**
 * Simulates the behavior of the CommandService classes by allowing the user to issue commands
 * through the publisher and receiving them synchronously through the receiver.
 * 
 * @author B-T-Johnson
 */
public class GenericProg
{
	private Configuration configuration;
	private String pubExchange;
	private String subExchange;
	private CommandServicePublisher pub;
	private CommandServiceSubscriber sub;
	private Map<String, CommandProcessor> systemCommands;
	
	public GenericProg(Configuration config, final String pubExchange, final String subExchange)
	{
		this.configuration = config;
		this.pubExchange = pubExchange;
		this.subExchange = subExchange;
		this.systemCommands = new HashMap<String, CommandProcessor>();
		this.pub = new CommandServicePublisher(config, pubExchange);
		this.sub = new CommandServiceSubscriber(config, subExchange)
		{
			public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, String message)
			{
				super.handleMessage(consumerTag, envelope, properties, message);
				
				CommandService msg = getMessage();
				CommandProcessor com = systemCommands.get(msg.getCommand());
				
				// TODO Command handling
		    	if(!msg.hasResponse() && msg.getDestination().equalsIgnoreCase(subExchange))
		    	{
		    		msg.setDestination(msg.getSource());
		    		msg.setSource(subExchange);
		    		
		    		if(msg.hasCommand())
		    		{	
		    			switch(msg.getCommand())
		    			{
		    				case "Quit":
		    					msg.setResponse("Terminate");
		    					break;
		    				default:
		    					msg.setResponse("Error");
		        				msg.setExplanation("Unexpected command");
		        				break;
		    			}
		    		}
		    		else
		        	{
		        		msg.setResponse("Error");
		        		msg.setExplanation("No command");
		        	}
		    	}
		    	//TODO Response handling
		    	else if(msg.hasResponse() && msg.getSource().equalsIgnoreCase(subExchange))
		    	{
		    		msg.setDestination(msg.getSource());
		    		msg.setSource(subExchange);
		    		
		    		if (com != null)
					{
						com.execute();
					}
					else
					{
						//TODO Error logging
						System.err.println("No process set to command '" + msg.getCommand() + "'");
					}
		    	}
		    	else
		    	{
		    		System.err.println("Cannot process message: not enough information given by '" + consumerTag + "'");
		    	}
		    	
				//System.err.println(msg);
			}
		};
		
		pub.start();
	}
	
	public GenericProg(Configuration config)
	{
		this(config, "Testing", "Testing");
	}
	
	public void setMessage(CommandService cmd)
	{
		pub.setMessage(cmd);
	}
	
	public CommandService getMessage()
	{
		return sub.getMessage();
	}
	
	public void setCommand(String command, CommandProcessor processor)
	{
		systemCommands.put(command, processor);
	}
	
	public static void main(String[] args)
	{
		Configuration config = new Configuration(args);
			@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);
		
		//Create generic program
		GenericProg prog = new GenericProg(config);
		
		//Add quit command
		QuitCommandProcessor basicQuit = new QuitCommandProcessor();
			prog.setCommand("Quit", basicQuit);
		
		System.out.println("==============");
		
		//Test
		while(true)
		{		
			//Initialize message command
			CommandService commander = new CommandService();
				commander.setSource("Keyboard");
				commander.setDestination("Testing");
				
				System.out.println("\tCreated new commander: " + commander.toJSONString());
				
			System.out.println("--------------");
			
			//Ask for command
			System.out.print("Send a command: ");
				String command = keyboard.nextLine();
				System.out.println();
			
			//Set command and send
			commander.setCommand(command);
				System.out.println("\t" + commander.toJSONString());
			prog.setMessage(commander);
			
			System.out.println("--------------");
			
			System.out.println("Waiting for response" + "\n");
			
			//Since pub creates a new thread, we must make the current thread sleep to synchronize them; TODO Thread with pub
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while(true)
			{
				if(prog.getMessage() != null)
				{
					System.out.println("--------------");
					
					System.out.println("Processing Command:" + "\n");
						prog.setMessage(prog.getMessage());
						break;
				}
				else
				{
					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("==============");
		}
	}
}