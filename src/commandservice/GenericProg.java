package commandservice;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import devdas.Configuration;
import devdas.LogPublisher;
import devdas.LogSubscriber;

/**
 * Generic program that processes commands received by a CommandServiceSubscriber and
 * sends the results through a CommandServicePublisher. The results of a command may be simple display of a variable's value or another command itself.
 * 
 * Made this a thread so that it can operate without blocking any other
 * functionality.
 * 
 * @author B-T-Johnson
 */
//TODO Operation command processing; allow operation commands to issue commands to other programs (when needed)
/*
 * ***MAJOR QUESTIONS***
 * Should this functionality be done within this class, or within the individual command processor object (since only the processor will know with whom it needs to talk)?
 * Should these type of processors change the destination when it need to do so? (this requires a standard exchange for programs, which is the "Operation" exchange)
 * How will it know who to talk to/what command to issue? (a static Registry within commandProcessor interface?)
 * Do system commands have this functionality (i.e., should one program tell another to start or stop)? Is this the significant difference between system/operation commands?
 * Will operation commands ever deal with the program directly (i.e., make changes to the behaviors of the program as a whole)? If not, should we change the execute() method and create a new abstract class to distinguish the two types of processors?
 */
public class GenericProg extends Thread //Should this implement Runnable instead?
{
		@SuppressWarnings("unused")
	private Configuration configuration; //Initializes RabbitMQ & exchanges
		@SuppressWarnings("unused")
	private String pubExchange, subExchange; //References to exchanges used by publisher and subscriber
	private CommandServicePublisher pub; //Sends messages
	private CommandServiceSubscriber sub; //Receives messages
	private LogPublisher logger; //Sends errors to log
	private Map<String, CommandProcessor> systemCommands; //System-wide commands like "Quit", "Start", "Status", "Pause"
	private Map<String, CommandProcessor> operationCommands; //Individual functions/behaviors of program
	private boolean running; //Checks to see if program thread is running
	private boolean operational; //Checks to see if the program can execute operation commands
	
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
			this.setSystemCommand("Quit", new QuitCommandProcessor()); //Are these the same command? Quit only ends the program thread
			this.setSystemCommand("Exit", new ExitCommandProcessor()); // "    "    "   "      "   ? Exit ends the whole application
			this.setSystemCommand("Status", new StatusCommandProcessor()); //Returns the current state of the program
			this.setSystemCommand("Start", new StartCommandProcessor()); //Starts the program (i.e., allows the program to process operations; the thread is already started)
		//	this.setSystemCommand("Pause", new PauseCommandProcessor()); //Halts any activity in the program (i.e., )
			//TODO Other system commands
		this.operationCommands = new HashMap<String, CommandProcessor>(); //Intentionally left blank by default
		this.pub = new CommandServicePublisher(config, pubExchange);
		this.sub = new CommandServiceSubscriber(config, subExchange);
		this.logger = new LogPublisher(config, config.getLogExchange());
		
		this.setName("Program Thread");
		
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
		//Initialize thread
		running = true;
		operational = false;
		
		logger.sendLogMessage("Program Start", "Started " + this.getClass().toString(), "Info");
		
		//Process
		while(running)
		{
			try
			{
				CommandServiceMessage message = sub.consumeMessage();
				
				if(message != null)
				{	
					if(!message.hasResponse() && message.getDestination().equalsIgnoreCase(this.toString()))
					{
						logger.sendLogMessage("Attempt", "Attempting to process command " + message.getCommandID(), "Info");
						
						processCommand(message);
						pub.setMessage(message);
						
						//TODO Error logging
						logger.sendLogMessage(message.getResponse().equals("Failure") ? "Error" : "Success", message.getResponse().equals("Failure") ? message.getExplanation() : "Successfully processed command " + message.getCommandID(), message.getResponse().equals("Failure") ? "High" : "Info");
					}
					
					message = null; //Resets message to avoid infinite loop
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
		systemCommands.put(command.toUpperCase(), processor);
	}
	
	/**
	 * Sets a {@link #CommandProcessor} as a value in the operation commands to the command key given by the String parameter
	 * 
	 * @param command name of the command to be processed
	 * @param processor name of the CommandProcessor set to the command parameter
	 */
	public void setOperationCommand(String command, CommandProcessor processor)
	{
		operationCommands.put(command.toUpperCase(), processor);
	}
	
	/**
	 * Observes the CommandService object passed to this method for any command that may exist for the object.
	 * If a command exists and this program can process the command, the program will process the command
	 * and a success or failure response will be assigned to the CommandService object depending on the success or failure upon execution.
	 * Otherwise, no action will be taken, and a failure response and explanation will be assigned.
	 * 
	 * @param msg the CommandService that contains the command
	 * @return Returns whether the command has been received by the program (also within the response field of the CommandService message)
	 */
	private String processCommand(CommandServiceMessage msg)
	{
		msg.setDestination(msg.getSource());
		msg.setSource(this.toString());
		String currentID = msg.getCommandID();

		if(msg.hasCommand())
		{
			if (systemCommands.get(msg.getCommand().toUpperCase()) != null) //Should system commands have priority over operation commands?
			{
				systemCommands.get(msg.getCommand().toUpperCase()).execute(this, msg);
			}
			else if(operationCommands.get(msg.getCommand().toUpperCase()) != null)
			{
				if (operational)
				{
					//TODO Operation command processing; allow operation commands to issue commands to other programs (when needed)
					/*
					 * ***MAJOR QUESTIONS***
					 * Should this functionality be done within this class, or within the individual command processor object (since only the processor will know with whom it needs to talk)?
					 * Should these type of processors change the destination when it need to do so? (this requires a standard exchange for programs, which is the "Operation" exchange)
					 * How will it know who to talk to/what command to issue? (a static Registry within commandProcessor interface?)
					 * Do system commands have this functionality (i.e., should one program tell another to start or stop)? Is this the significant difference between system/operation commands?
					 * Will operation commands ever deal with the program directly (i.e., make changes to the behaviors of the program as a whole)? If not, should we change the execute() method and create a new abstract class to distinguish the two types of processors?
					 */
					operationCommands.get(msg.getCommand().toUpperCase()).execute(this, msg);
				}
				else
				{
					msg.setResponse("Failure");
					msg.setExplanation("Program is not operational");
				}
			}
			else
			{
				msg.setResponse("Failure");
				msg.setExplanation("Unexpected command: " + msg.getCommand().toUpperCase());
			}
		}
		else
		{
			msg.setResponse("Failure");
			msg.setExplanation("No command");
		}
		
		//Resets command field if there is no new command; prevents accidental processing between programs
		if (msg.getCommandID().equals(currentID))
		{
			msg.setCommand(null);
		}
		
		return msg.getExplanation();
	}
	
	/**
	 * @param running The setter to indicate that the program thread should be running
	 */
	public void setRunning(boolean running)
	{
		this.running = running;
	}
	
	/**
	 * @return Returns whether the program thread is running
	 */
	public boolean isRunning()
	{
		return this.running;
	}
	
	/**
	 * @param operational The setter to indicate that the program can process operational commands
	 */
	public void setOperational(boolean operational)
	{
		this.operational = operational;
	}
	
	/**
	 * @return Returns whether the program thread can process operational commands
	 */
	public boolean isOperational()
	{
		return this.operational;
	}
	
	/**
	 * @return Returns a list of the current commands within the known system commands
	 */
	public String getSystemCommands()
	{
		return systemCommands.keySet().toString();
	}
	
	/**
	 * @return Returns a list of the current commands within the known operation commands
	 */
	public String getOperationCommands()
	{
		return operationCommands.keySet().toString();
	}
	
	/**
	 * Used for debugging and testing the class code
	 * 
	 * @param args - command line arguments
	 */
	// TODO replace with junit testing
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
			e.printStackTrace();
		}
		
		System.out.println("==============");
		
		System.out.println("Known commands are listed below:");
		System.out.println("\tSYSTEM COMMANDS: " + prog.getSystemCommands());
		System.out.println("\tOPERATION COMMANDS: " + prog.getOperationCommands());
		
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