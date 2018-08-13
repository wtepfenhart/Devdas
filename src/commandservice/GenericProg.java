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
		@SuppressWarnings("unused")
	private Configuration configuration;
		@SuppressWarnings("unused")
	private String pubExchange;
	private String subExchange;
	private CommandServicePublisher pub;
	private CommandServiceSubscriber sub;
	private Map<String, CommandProcessor> systemCommands;
	
	public GenericProg(Configuration config, String pubExchange, String subExchange)
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
				
				Thread programThread = new Thread() //Concurrent thread
				{
					@Override
					public void run()
					{
						if(receiveCommand(getMessage()))
						{
							GenericProg.this.publish(getMessage());
						}
						else
						{
							processCommand(getMessage());
						}
				    }
				};
				
				programThread.start();
			}
		};
		
		pub.start();
	}
	
	public GenericProg(Configuration config)
	{
		this(config, "Testing", "Testing");
	}
	
	
	/**
	 * Sends a message as a {@link #CommandService} to the exchange set for the program
	 * 
	 * @param cmd message to be sent to the exchange
	 */
	public void publish(CommandService cmd)
	{
		pub.setMessage(cmd);
	}
	
	/**
	 * Sends a message as a JSON String to the exchange set for the program
	 * 
	 * @param jsonStr message to be sent to the exchange
	 */
	public void publish(String jsonStr)
	{
		pub.setMessage(jsonStr);
	}
	
	/**
	 * Sets a {@link #CommandProcessor} as a value to the command key given by the String parameter
	 * 
	 * @param command name of the command to be processed
	 * @param processor name of the CommandProcessor set to the command parameter
	 */
	public void setCommand(String command, CommandProcessor processor)
	{
		systemCommands.put(command, processor);
	}
	
	/**
	 * Processes the CommandService object passed to this method for any command that may exist in the object
	 * 
	 * @param msg the CommandService that contains the command
	 * @return Returns whether the command has been processed by the program
	 */
	public void processCommand(CommandService msg)
	{
		CommandProcessor com = systemCommands.get(sub.getMessage().getCommand());
		
		if (com != null)
		{
			com.execute(GenericProg.this, sub.getMessage());
		}
		else
		{
			//TODO Error logging
			System.err.println("No process set to command '" + sub.getMessage().getCommand() + "'");
		}
	}
	
	/**
	 * Observes the CommandService object passed to this method for any command that may exist in the object
	 * 
	 * @param msg the CommandService that contains the command
	 * @return Returns whether the command has been received by the program
	 */
	public boolean receiveCommand(CommandService msg)
	{
		boolean isReceived = false;
		
		// TODO Command handling
		if(!msg.hasResponse() && msg.getDestination().equalsIgnoreCase(subExchange))
		{
			isReceived = true;

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
		
		return isReceived;
	}
	
	public static void main(String[] args)
	{
		Configuration config = new Configuration(args);
			@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);
			
		//Ask user for exchanges
		System.out.print("Name the receiver to use: ");
			String userSubExchange = keyboard.nextLine();
		System.out.print("Name the publisher to use: ");
			String userPubExchange = keyboard.nextLine();
		
		System.out.println();
		
		//Create generic program
		GenericProg prog = new GenericProg(config, userPubExchange, userSubExchange);
		
		//Since program creates a new thread, we can make the current thread sleeps to synchronize the threads (arbitrary, but cleans up the display)
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Add "Quit" command
		QuitCommandProcessor basicQuit = new QuitCommandProcessor();
			prog.setCommand("Quit", basicQuit);
		
		System.out.println("==============");
		
		//Test
		while(true)
		{		
			//Initialize message command
			CommandService commander = new CommandService();
				commander.setSource(userSubExchange);
				commander.setDestination(userPubExchange);
				
				System.out.println("\tCreated new commander: " + commander.toJSONString());
				
			System.out.println("--------------");
			
			//Ask for command
			System.out.print("Send a command: ");
				String command = keyboard.nextLine();
				System.out.println();
			
			//Set command and send
			commander.setCommand(command);
				System.out.println(" [x] Sent " + commander.toJSONString());
			prog.publish(commander);
			
			System.out.println("--------------");
			
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			System.out.println("==============");
		}
	}
}