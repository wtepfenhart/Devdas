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
 * The results of a command may be a simple display of a variable's value or another command itself.
 * 
 * Made this implement the Runnable interface so that it can operate without blocking any other
 * functionality.
 * 
 * @author B-T-Johnson
 */
/*
 * ***MAJOR QUESTIONS***
 * Should operations be done within programs, or within the individual command processor object?
 * Should operation processors change the destination when it need to talk to other programs (this requires a standard exchange for programs, which is the "Operation" exchange)?
 * How will it know who to talk to/what command to issue? A static Registry within commandProcessor interface, or within GenericProg? Or a whole new ProgramRegistry class?
 * Should the destination field in CommandService messages be an array (since there may be messages sent to multiple programs at once)? 
 * Do system commands have this functionality (i.e., should one program tell another to start or stop)? Is this a significant difference between system/operation commands?
 * Will operation commands ever deal with the program directly (i.e., make changes to the behaviors of the program as a whole)? If not, should we change the execute() method and create two new abstract classes to distinguish the two types of processors?
 * How should a command have multiple parameters (i.e., a "Speak" command might include a description of what to say)? Would the description be included in the explanation field of the CommandService message?
 * How would the registry distinguish commands? By a ProcessID (which would be a new field in CommandService messages), or by the CommandProcessor in use (a reference to the object's name)?
 */
public class GenericProg implements Runnable
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
			this.setSystemCommand("Report", new StatusCommandProcessor()); //Returns the current state of the program
			this.setSystemCommand("Start", new StartCommandProcessor()); //"Starts" the program; does not start the program thread, but instead allows the program to process operation commands (might need refactoring)
			this.setSystemCommand("Pause", new PauseCommandProcessor()); //Halts any *further* activity in the program (i.e., prevents the program from processing any additional operational commands)
		//  this.setSystemCommand("Log", new LogCommandProcessor()); //Logs *what* exactly? Any extra information that the program wants to pass to the logger?
		//	this.setSystemCommand("Stop", new StopCommandProcessor()); //Stops a program from processing one or all commands(would require interrupt() method)
			//TODO Other system commands
		this.operationCommands = new HashMap<String, CommandProcessor>(); //Intentionally left blank by default
		this.pub = new CommandServicePublisher(config, pubExchange);
		this.sub = new CommandServiceSubscriber(config, subExchange);
		this.logger = new LogPublisher(config, config.getLogExchange());
		
		pub.start();
		logger.start();
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
	
	public GenericProg(Configuration config, String[] commands, CommandProcessor[] opProcessors)
	{
		this(config);
		
		for (int i = 0; i < commands.length && i < opProcessors.length; i++) //TODO Check "off-by-one"
		{
			this.setOperationCommand(commands[i], opProcessors[i]);
		}
	}
	
	@Override
	public void run()
	{
		//Initialize states
		running = true;
		operational = false;
		
		logger.sendLogMessage("Program Start", "Started " + this.toString(), "Info");
		
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
						logger.sendLogMessage("Attempt", this.toString() + " attempting to process command " + message.getCommandID(), "Info");
						
						processCommand(message);
						
						//Notify source with results of processing
							pub.setMessage(message);
						
							//TODO Error logging
							if(message.getResponse().equals("Failure"))
							{
								logger.sendLogMessage("Error", this.toString() + " unable to process command " + message.getCommandID(), "High");
							}
							else
							{
								logger.sendLogMessage("Success" , this.toString() + " successfully processed command " + message.getCommandID(), "Info");
							}
					}
					
					message = null; //Resets message to avoid infinite loop
				}
				else
				{
					Thread.sleep(10);
				}
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
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
	public Map<String, CommandProcessor> getSystemCommands()
	{
		return systemCommands;
	}
	
	/**
	 * @return Returns a list of the current commands within the known operation commands
	 */
	public Map<String, CommandProcessor> getOperationCommands()
	{
		return operationCommands;
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
			
		//Create generic programs
		GenericProg prog = new GenericProg(config, source, destination);
			Thread thread1 = new Thread(prog);
		GenericProg dummy = new GenericProg(config, source, destination); //Tests for accidental multiprogram execution
			Thread thread2 = new Thread(dummy);
		
		//Start separate threads for programs
		thread1.start();
		thread2.start();
			
		//Since the program exists in a concurrent thread, we can make the current thread sleep to synchronize the threads (arbitrary, but cleans up the display)
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
		
		System.out.println("\tSYSTEM COMMANDS: " + prog.getSystemCommands().keySet());
		
		System.out.println("\tOPERATION COMMANDS: " + prog.getOperationCommands().keySet());
		
		
		System.out.println("==============");
		
		//Test
		while(prog.isRunning() && dummy.isRunning())
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
			if(command.contains(" ")) //Command has an explanation
			{
				commander.setCommand(command.substring(0, command.indexOf(" ")));
				commander.setExplanation(command.substring(command.indexOf(" ") + 1));
			}
			else
			{
				commander.setCommand(command);
			}
			
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