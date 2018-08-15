package commandservice;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import devdas.Configuration;

/**
 * Generic program that processes commands received by a CommandServiceSubscriber and
 * sends the results through a CommandServicePublisher.
 * 
 * Made this a thread so that it can operate without blocking any other
 * functionality.
 * 
 * @author B-T-Johnson
 */
public class GenericProg extends Thread
{
		@SuppressWarnings("unused")
	private Configuration configuration;
		@SuppressWarnings("unused")
	private String pubExchange;
	private String subExchange;
	private CommandServicePublisher pub;
	private CommandServiceSubscriber sub;
	private Map<String, CommandProcessor> systemCommands;
	private boolean isRunning;
	
	public GenericProg(Configuration config, String pubExchange, String subExchange)
	{
		this.configuration = config;
		this.pubExchange = pubExchange;
		this.subExchange = subExchange;
		this.systemCommands = new HashMap<String, CommandProcessor>();
		this.pub = new CommandServicePublisher(config, pubExchange);
		this.sub = new CommandServiceSubscriber(config, subExchange);
		
		pub.start();
		this.start();
	}
	
	public GenericProg(Configuration config)
	{
		this(config, "Testing", "Testing");
	}
	
	@Override
	public void run()
	{	
		isRunning = true;
		
		while(isRunning)
		{
			try
			{
				CommandService message = sub.getMessage();
				
				if(message != null)
				{	
					if(!message.hasResponse() && message.getDestination().equalsIgnoreCase(subExchange))
					{
						processCommand(message);
						publish(message);
					}
					
					message = null;
				}
				else
				{
					sleep(10);
				}
			}
			catch (InterruptedException e)
			{
				currentThread().interrupt();
				e.printStackTrace();
			}
		}
		
		System.out.println("Program is no longer running");
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
	 * Observes the CommandService object passed to this method for any command that may exist for the object.
	 * If a command exists and this program can process the command, the command will be processed by the program
	 * and a success response will be assigned to the CommandService object. Otherwise, no action will be taken,
	 * and a failure response and explanation will be assigned.
	 * 
	 * @param msg the CommandService that contains the command
	 * @return Returns whether the command has been received by the program
	 */
	private void processCommand(CommandService msg)
	{
		msg.setDestination(msg.getSource());
		msg.setSource(subExchange);

		if(msg.hasCommand())
		{	
			if (systemCommands.get(msg.getCommand()) != null)
			{
				msg.setResponse("Success");
				systemCommands.get(msg.getCommand()).execute(this, msg);
			}
			else
			{
				//TODO Error logging
				msg.setResponse("Error");
				msg.setExplanation("Unexpected command '" + msg.getCommand() + "'");
			}
		}
		else
		{
			msg.setResponse("Error");
			msg.setExplanation("No command");
		}
	}
	
	public void setRunning(boolean status)
	{
		this.isRunning = status;
	}
	
	public boolean isRunning()
	{
		return this.isRunning;
	}
	
	public static void main(String[] args)
	{
		final Scanner keyboard = new Scanner(System.in);
		
		Configuration config = new Configuration(args);
		String source = "Keyboard", destination = "Program";
		CommandServicePublisher userPub = new CommandServicePublisher(config, destination);
		CommandServiceSubscriber userSub = new CommandServiceSubscriber(config, source);
			userPub.start();
			
		//Create generic program
		GenericProg prog = new GenericProg(config, source, destination);
		
		//Since program creates a new thread, we can make the current thread sleep to synchronize the threads (arbitrary, but cleans up the display)
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Add "Quit" command to program
		QuitCommandProcessor basicQuit = new QuitCommandProcessor();
			prog.setCommand("Quit", basicQuit);
		
		QuitCommandProcessor smartQuit = new QuitCommandProcessor()
		{
			public void execute(GenericProg prog, CommandService msg)
			{
				prog.setRunning(false);
			}
		};
			prog.setCommand("Safe Quit", smartQuit);
		
		System.out.println("==============");
		
		//Test
		while(prog.isRunning())
		{
			//Initialize message command
			CommandService commander = new CommandService();
				commander.setSource(source);
				commander.setDestination(destination);
				System.out.println("\tCreated new commander: " + commander.toJSONString());
				
			System.out.println("--------------");
			
			//Ask for command
			System.out.print("Send a command: ");
				String command = keyboard.nextLine();
				System.out.println();
			
			//Set command and send
			commander.setCommand(command);
				System.out.println(" [" + source + "] Sent " + commander.toJSONString());
			userPub.setMessage(commander);
			
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
		
		keyboard.close();
		System.exit(1);
	}
}