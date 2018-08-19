package commandservice;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import devdas.Configuration;
import devdas.LogPublisher;
import devdas.LogSubscriber;

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
	private String pubExchange, subExchange;
	private CommandServicePublisher pub;
	private CommandServiceSubscriber sub;
	private LogPublisher logger;
	private Map<String, CommandProcessor> systemCommands; //System-wide commands like "Quit", "Start", "Status", "Pause"
	private Map<String, CommandProcessor> operationCommands; //Individual functions/behaviors
	private boolean isRunning;
	private boolean isOperational;
	
	/**
	 * Creates a new Generic Program set by the Configuration object, and allows the ability to set the Publisher and Subscriber to specific exchanges 
	 * 
	 * @param config Configuration object to set
	 * @param pubExchange Exchange for the Publisher
	 * @param subExchange Exchange for the Subscriber
	 */
	public GenericProg(Configuration config, String pubExchange, String subExchange)
	{
		this.configuration = config;
		this.pubExchange = pubExchange;
		this.subExchange = subExchange;
		this.systemCommands = new HashMap<String, CommandProcessor>();
			this.setSystemCommand("Quit", new QuitCommandProcessor());
			this.setSystemCommand("Exit", new ExitCommandProcessor());
			this.setSystemCommand("Status", new StatusCommandProcessor());
			this.setSystemCommand("Start", new StartCommandProcessor());
		//	this.setSystemCommand("Pause", new PauseCommandProcessor(this));
			//TODO Other system commands
		this.operationCommands = new HashMap<String, CommandProcessor>(); //Intentionally left blank by default
		this.pub = new CommandServicePublisher(config, pubExchange);
		this.sub = new CommandServiceSubscriber(config, subExchange);
		this.logger = new LogPublisher(config, config.getLogExchange());
		
		pub.start();
		logger.start();
		this.start();
	}
	
	/**
	 * Creates a new Generic Program set by the Configuration object
	 * 
	 * @param config Configuration object to set
	 */
	public GenericProg(Configuration config)
	{
		this(config, config.getExchange(), config.getOperationExchange());
	}
	
	public GenericProg(Configuration config, String[] commands, CommandProcessor[] processors)
	{
		this(config);
		
		for (int i = 0; i < commands.length && i < processors.length; i++) //TODO Check "off-by-one"
		{
			this.setOperationCommand(commands[i], processors[i]);
		}
	}
	
	@Override
	public void run()
	{	
		isRunning = true;
		
		logger.sendLogMessage("Start", "Started GenericProg", "Info");
		
		while(isRunning)
		{
			try
			{
				CommandServiceMessage message = sub.consumeMessage();
				
				if(message != null)
				{	
					if(!message.hasResponse() && message.getDestination().equalsIgnoreCase(this.toString()))
					{
						processCommand(message);
						pub.setMessage(message);
						//TODO Error logging
						logger.sendLogMessage(message.getResponse().equals("Failure") ? "Error" : "Success", message.getExplanation(), message.getResponse().equals("Failure") ? "High" : "Info");
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
	 * Sets a {@link #CommandProcessor} as a value in the system commands to the command key given by the String parameter
	 * 
	 * @param command name of the command to be processed
	 * @param processor name of the CommandProcessor set to the command parameter
	 */
	public void setSystemCommand(String command, CommandProcessor processor)
	{
		systemCommands.put(command, processor);
	}
	
	/**
	 * Sets a {@link #CommandProcessor} as a value in the operation commands to the command key given by the String parameter
	 * 
	 * @param command name of the command to be processed
	 * @param processor name of the CommandProcessor set to the command parameter
	 */
	public void setOperationCommand(String command, CommandProcessor processor)
	{
		operationCommands.put(command, processor);
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
	private void processCommand(CommandServiceMessage msg)
	{
		msg.setDestination(msg.getSource());
		msg.setSource(this.toString());

		if(msg.hasCommand())
		{
			if (systemCommands.get(msg.getCommand()) != null) //System commands should have priority over operation commands
			{
				systemCommands.get(msg.getCommand()).execute(this, msg);
			}
			else if(isOperational == true && operationCommands.get(msg.getCommand()) != null)
			{
				operationCommands.get(msg.getCommand()).execute(this, msg);
			}
			else
			{
				msg.setResponse("Failure");
				msg.setExplanation("Unexpected command: " + msg.getCommand());
			}
		}
		else
		{
			msg.setResponse("Failure");
			msg.setExplanation("No command");
		}
	}
	
	public void setRunning(boolean state)
	{
		this.isRunning = state;
	}
	
	public boolean isRunning()
	{
		return this.isRunning;
	}
	
	public void setOperational(boolean state)
	{
		this.isOperational = state;
	}
	
	public boolean isOperational()
	{
		return this.isOperational;
	}
	
	public static void main(String[] args)
	{
		final Scanner keyboard = new Scanner(System.in);
		String source = "Keyboard", destination = "Program";
		
		Configuration config = new Configuration(args);
		CommandServicePublisher userPub = new CommandServicePublisher(config, destination);
		CommandServiceSubscriber userSub = new CommandServiceSubscriber(config, source);
			@SuppressWarnings("unused")
		LogSubscriber logger = new LogSubscriber(config, "Logging");
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
		
		System.out.println("==============");
		
		//Test
		while(prog.isRunning())
		{
			//Initialize message command
			CommandServiceMessage commander = new CommandServiceMessage();
				commander.setSource(userSub.toString());
				commander.setDestination(prog.toString());
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