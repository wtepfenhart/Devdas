package commandservice;

import java.lang.Thread.State;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import devdas.Configuration;
import devdas.LogPublisher;
import devdas.LogSubscriber;

/**
 * Generic program that processes commands received by a CommandServiceSubscriber and sends the results through a CommandServicePublisher.
 * The results of a command may be a simple display of a variable's value or another command itself.
 * 
 * @author B-T-Johnson
 */
/*
 * ***MAJOR QUESTIONS***
 * How should processors "talk" to each other (if they need to)? Should processors send a new command through the program? Or should they deal directly with the processor themselves (Right now, all processors can communicate directly with each other, but this isn't very flexible)?
 * How will it know who to talk to/what command to issue? A static Registry within commandProcessor interface, or within GenericProg? Or a whole new Program/ProcessorRegistry class?
 * Can the destination field in CommandService messages be an array (since there may be messages for multiple programs at once)? Or should it just be an Object to allow any type of data through? (PROBLEM: when passed through RabbitMQ, the data can ONLY be written as a String)
 * Will operation commands ever deal with the program directly (i.e., make changes to the behaviors of the program as a whole)? If not, should we change the execute() method to reflect this? **DONE**
 * What do operation commands have in common that system commands don't, and vice versa?
 * How would the registry distinguish between processors? By a ProcessID (a new field in CommandService messages) appended to the command when sent, or by the individual processor when received?
 * Should processors be Threads? Should processors run concurrently (i.e., each processor can operate separately from the others), or should there only be one processor running at any one time (since multi-threading may slow down the machine excessively)?
 * Should a program that has stopped running still be listening to messages being sent (since another program may request this program to start again)? 
 */
public class GenericProg
{
		@SuppressWarnings("unused")
	private Configuration configuration; //Initializes RabbitMQ & exchanges
		@SuppressWarnings("unused")
	private String pubExchange, subExchange; //Temporary place-holders for the exchanges used by publisher and subscriber; TODO Remove when using Configuration
	private CommandServicePublisher pub; //Sends messages
	private CommandServiceSubscriber sub; //Receives messages
	private LogPublisher logger; //Sends information to log
	private Map<String, SystemCommandProcessor> systemCommands; //System-wide commands like "Quit", "Start", "Status", "Pause"
	private Map<String, OperationCommandProcessor> operationCommands; //Individual functions/behaviors of program
	private boolean running; //Checks to see if program is running (can process commands)
	
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
		this.systemCommands = new HashMap<String, SystemCommandProcessor>();
			this.setSystemCommand("Quit", new PerformStopCommandProcessor(this)); //Are these the same command? Stop only ends a singular program by changing its running variable
			this.setSystemCommand("Exit", new PerformExitCommandProcessor(this)); // "    "    "   "      "   ? Exit ends the whole application
			this.setSystemCommand("Status", new PerformStatusCommandProcessor(this)); //Returns the current state of a specific processor
			this.setSystemCommand("Report", new PerformReportCommandProcessor(this)); //Returns the current state of the program
/***FIX***/	this.setSystemCommand("Resume", new PerformResumeCommandProcessor(this)); //Allows a processor to continue processing after it has been paused
			this.setSystemCommand("Start", new PerformStartCommandProcessor(this)); //Starts a program after it has been stopped
/***FIX***/	this.setSystemCommand("Pause", new PerformPauseCommandProcessor(this)); //Temporarily halts a processor from processing a command
			this.setSystemCommand("Log", new PerformLogCommandProcessor(this)); //Sends a log message
			//TODO Other system commands
		this.operationCommands = new HashMap<String, OperationCommandProcessor>(); //Intentionally left blank by default
		this.pub = new CommandServicePublisher(config, pubExchange);
		this.sub = new CommandServiceSubscriber(config, subExchange)
		{
			@Override
			public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, String message)
			{
				super.handleMessage(consumerTag, envelope, properties, message);
				
				receiveMessage();
			}
		};
			
		this.logger = new LogPublisher(config, config.getLogExchange());
		
		pub.start();
		logger.start();
		
		setRunning(true);
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
	
	public GenericProg(Configuration config, String[] commands, OperationCommandProcessor[] opProcessors)
	{
		this(config);
		
		for (int i = 0; i < commands.length && i < opProcessors.length; i++) //TODO Check "off-by-one"
		{
			this.setOperationCommand(commands[i], opProcessors[i]);
		}
	}
	
	/**
	 * Performs the necessary actions to receive a message and begin command processing, as well as notifying the logger of all event cases.
	 * 
	 * See {@link #processCommand(CommandServiceMessage)} to see how the message is processed.
	 */
	public void receiveMessage() //Will this method ever be changed or used outside of this generic?
	{
		CommandServiceMessage msg = sub.consumeMessage();
		
		if(msg != null)
		{	
			if(!msg.hasResponse() && msg.getDestination().equalsIgnoreCase(this.toString()))
			{
				this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process command " + msg.getCommandID(), "Info");
					
				processCommand(msg);
					
				//Notify source with results of processing
				sendMessage(msg);
					
				//TODO Error logging; should the individual command processor issue the log message?
				if(msg.getResponse().equals("Failure"))
				{
					this.logger.sendLogMessage("Error", this.toString() + " unable to process command " + msg.getCommandID(), "High");
				}
				else
				{
					this.logger.sendLogMessage("Success" , this.toString() + " successfully processed command " + msg.getCommandID(), "Info");
				}
			}
				
			msg = null; //Resets message to avoid infinite loop
		}
	}
	
	/**
	 * Sets a {@link #CommandProcessor} as a value in the system commands to the command key given by the String parameter
	 * 
	 * @param command name of the command to be processed
	 * @param processor name of the CommandProcessor set to the command parameter
	 */
	public void setSystemCommand(String command, SystemCommandProcessor processor)
	{
		systemCommands.put(command.toUpperCase(), processor);
	}
	
	/**
	 * Sets a {@link #CommandProcessor} as a value in the operation commands to the command key given by the String parameter
	 * 
	 * @param command name of the command to be processed
	 * @param processor name of the CommandProcessor set to the command parameter
	 */
	public void setOperationCommand(String command, OperationCommandProcessor processor)
	{
		operationCommands.put(command.toUpperCase(), processor);
	}
	
	/**
	 * Observes the CommandService object passed to this method for any command that may exist for the object.
	 * If the command exists within the program, the program will process the command and a success or failure response will be assigned to the CommandService object, depending on the success or failure upon execution.
	 * Otherwise, no action will be taken, and a failure response and explanation will be assigned.
	 * 
	 * @param msg the CommandService message that contains the command
	 * @return Returns if the program is running when processing the command
	 */
	public boolean processCommand(CommandServiceMessage msg) //Will this method ever be changed or used outside of this generic?
	{
		msg.setDestination(msg.getSource());
		msg.setSource(this.toString());
		String currentID = msg.getCommandID();

		if(msg.hasCommand())
		{
			if (systemCommands.get(msg.getCommand().toUpperCase()) != null) //Should system commands have priority over operation commands?
			{
				systemCommands.get(msg.getCommand().toUpperCase()).execute(msg);
			}
			else if(operationCommands.get(msg.getCommand().toUpperCase()) != null)
			{
				operationCommands.get(msg.getCommand().toUpperCase()).execute(msg);
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

		//Resets command field if the command has not been reissued; prevents accidental re-execution between programs
		if (msg.getCommandID().equals(currentID))
		{
			msg.setCommand(null);
		}
		
		return isRunning();
	}
	
	/**
	 * @param running The setter to indicate that the program thread should be running
	 */
	public void setRunning(boolean running)
	{
		if(running)
		{
			this.logger.sendLogMessage("Program Start", "Started " + this.toString(), "Info");
		}
		else
		{
			this.logger.sendLogMessage("Program End", "Ended " + this.toString(), "Info");
		}
		
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
	 * Sends a command through the publisher associated with this program as a CommandServiceMessage
	 * 
	 * @param cmd The CommandServiceMessage to send
	 */
	public void sendMessage(CommandServiceMessage cmd)
	{
		pub.setMessage(cmd);
	}

////////////////////////////*METHODS USED BY COMMAND PROCESSORS*////////////////////////////
	/**
	 * Handles sending a log message to the log exchange associated with the program; used by processors)
	 * 
	 * Will throw an exception if the log message cannot be sent
	 * 
	 * @param evnt The general type of message to be sent
	 * @param msg A description of the event or any extraneous information associated with the event
	 * @param severity The level of importance that this event should possess
	 */
	public void log(String evnt, String msg, String severity) throws Exception
	{
		logger.sendLogMessage(evnt, msg, severity);
	}
	
	/**
	 * Handles terminating the whole application with no clean-up; used if in case of critical errors
	 * 
	 * Will throw an exception if it cannot exit out of the application
	 */
	public void exit() throws Exception
	{
		System.exit(1);
	}
	
	/**
	 * Handles terminating an individual program with clean-up
	 * 
	 * Will throw an exception if the program cannot stop
	 */
	public void stop() throws Exception
	{
		if(this.isRunning())
		{
			this.setRunning(false);
			this.pub.setRunning(false);
			this.logger.setRunning(false);
		}
		else
		{
			throw new Exception(this.toString() + "is no longer running");
		}
	}
	
	/**
	 * Handles starting a program if it has been stopped
	 * 
	 * Will throw an exception if it cannot start the program
	 */
	public void start() throws Exception
	{
		if(this.isRunning())
		{
			throw new Exception(this.toString() + " is already running"); //Cannot start a program that is already running
		}
		else
		{
			this.setRunning(true);
			this.pub.setRunning(true);
			this.logger.setRunning(true);
		}
	}
	
	/**
	 * Handles temporarily halting an individual process
	 * 
	 * Will throw an exception if the process cannot be paused
	 * 
	 * @param processName Identifier for the process
	 */
	@SuppressWarnings("deprecation")
	public void pause(String processName) throws Exception //TODO Need to fix
	{
		if(this.isRunning())
		{
			Thread sys = ((SystemCommandProcessor) this.systemCommands.get(processName.toUpperCase())).getProcessingThread();
			Thread opt = ((OperationCommandProcessor) this.operationCommands.get(processName.toUpperCase())).getProcessingThread();

			if(sys != null)
			{	
				if(sys.getState().equals(State.RUNNABLE))
				{
					sys.suspend(); //TODO Need to fix
				}
			}
			else if(opt != null)
			{	
				if(opt.getState().equals(State.RUNNABLE))
				{
					opt.suspend(); //TODO Need to fix
				}
			}
			else
			{
				throw new Exception("Cannot pause " + processName); //Cannot pause something that doesn't exist or that isn't already running
			}
		}
		else
		{
			throw new Exception(this.toString() + "is no longer running");
		}
	}
	
	/**
	 * Handles sending and returning a notification whether a program is running
	 * 
	 * Will throw an exception if it cannot retrieve the running state
	 */
	public String report() throws Exception //Should this method still be able to send out a message to the logger even if the program has stopped?
	{
		this.log("Report", this.toString() + " is " + (this.isRunning() ? "RUNNING" : "NOT RUNNING"), "Info"); //TODO Refactor "not running" to something more succinct
		
		return this.isRunning() ? "RUNNING" : "NOT RUNNING";
	}
	/**
	 * Handles resuming a process after it has been paused
	 * 
	 * Will throw an exception if it cannot resume the process
	 * 
	 * @param processName Identifier for the process
	 */
	@SuppressWarnings("deprecation")
	public void resume(String processName) throws Exception //TODO Need to fix
	{
		if(this.isRunning())
		{
			Thread sys = ((SystemCommandProcessor) this.systemCommands.get(processName.toUpperCase())).getProcessingThread();
			Thread opt = ((OperationCommandProcessor) this.operationCommands.get(processName.toUpperCase())).getProcessingThread();

			if(sys != null)
			{	
				if(!sys.getState().equals(State.RUNNABLE))
				{
					sys.resume(); //TODO Need to fix
				}

			}
			else if(opt != null)
			{	
				if(!opt.getState().equals(State.RUNNABLE))
				{
					opt.resume(); //TODO Need to fix
				}
			}
			else
			{
				throw new Exception("Cannot resume " + processName); //Cannot start something that doesn't exist or that is already running
			}
		}
		else
		{
			throw new Exception(this.toString() + "is no longer running");
		}
	}
	
	public State status(String processName) throws Exception
	{
		if(this.isRunning())
		{
			Thread sys = ((SystemCommandProcessor) this.systemCommands.get(processName.toUpperCase())).getProcessingThread();
			Thread opt = ((OperationCommandProcessor) this.operationCommands.get(processName.toUpperCase())).getProcessingThread();

			if(sys != null)
			{
				this.log("Status", sys.toString() + " is " + (sys.isAlive() ? "ALIVE" : sys.isInterrupted() ? "INTERRUPTED" : sys.isDaemon() ? "DAEMON" : "DEAD") + " and " + sys.getState(), "Info"); //Should we log the status of the processor?
				return sys.getState();
			}
			else if(opt != null)
			{
				this.log("Status", opt.toString() + " is " + (opt.isAlive() ? "ALIVE" : opt.isInterrupted() ? "INTERRUPTED" : opt.isDaemon() ? "DAEMON" : "DEAD") + " and " + opt.getState(), "Info");
				return opt.getState();
			}
			else
			{
				throw new Exception("Unexpected command: " + processName);
			}
		}
		else
		{
			throw new Exception(this.toString() + "is no longer running");
		}
	}
	
	public void doSomething() throws Exception
	{
		System.err.println("DOING SOMETHING");
		Thread.sleep(10000);
	}

////////////////////////////*END OF METHODS USED BY COMMAND PROCESSORS*////////////////////////////
	
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
		GenericProg dummy = new GenericProg(config, source, destination); //Tests for accidental multiprogram execution
		
		prog.setOperationCommand("Do", new PerformDoCommandProcessor(prog)); //Dummy processor; used for testing Pause and Resume
			
		//Since there are concurrent thread, we can make the current thread sleep to partially synchronize the threads (arbitrary, but cleans up the display)
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
		while(true)
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
			if(command.contains(" ")) //Command has an option
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
	}
}