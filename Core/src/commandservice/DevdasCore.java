package commandservice;

import java.util.HashMap;
import java.util.Map;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import devdas.Configuration;
import devdas.LogPublisher;

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
public abstract class DevdasCore
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
	private String logLevel; //IS THIS ACTUALLY A STRING?
	
	/**
	 * Creates a new Generic Program set by the Configuration object, and allows the ability to set the Publisher and Subscriber to specific exchanges 
	 * 
	 * @param config Configuration object to set
	 * @param pubExchange Exchange for the Publisher
	 * @param subExchange Exchange for the Subscriber
	 */
	public DevdasCore(Configuration config, String pubExchange, String subExchange)
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
			this.setSystemCommand("Log", new PerformSetLogLevelCommandProcessor(this)); //Sends a log message
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
		this.logLevel = "Info";
		
		pub.start();
		logger.start();
		
		setRunning(true);
	}
	
	/**
	 * Creates a new Generic Program set by the Configuration object
	 * 
	 * @param config Configuration object to set
	 */
	public DevdasCore(Configuration config)
	{
		this(config, config.getExchange(), config.getOperationExchange());
	}
	
	public DevdasCore(Configuration config, String[] commands, OperationCommandProcessor[] opProcessors)
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
			if(msg.isCommand())
			{		
				processCommand(msg);
					
				//Notify source with results of processing
				sendMessage(msg);
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
		msg.addParam("Destination", msg.getParam("Source"));
		msg.addParam("Source", this.toString());
		String currentID = msg.getParam("CommandID");

		if(msg.isCommand())
		{
			if (systemCommands.get(msg.getParam("Command").toUpperCase()) != null) //Should system commands have priority over operation commands?
			{
				systemCommands.get(msg.getParam("Command").toUpperCase()).execute(msg);
			}
			else if(operationCommands.get(msg.getParam("Command").toUpperCase()) != null)
			{
				operationCommands.get(msg.getParam("Command").toUpperCase()).execute(msg);
			}
			else
			{
				msg.addParam("Response", "Failure");
				msg.addParam("Explanation", "Unexpected command: " + msg.getParam("Command").toUpperCase());
			}
		}
		else
		{
			msg.addParam("Response","Failure");
			msg.addParam("Explanation", "No command");
		}

		//Resets command field if the command has not been reissued; prevents accidental re-execution between programs
		if (msg.getParam("CommandID").equals(currentID))
		{
			msg.addParam("CurrentID", null);
		}
		
		return isRunning();
	}
	
	public void sendLogMessage(String event, String message, String severity)
	{
		this.logger.sendLogMessage(event, message, severity);
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
	public String getSystemCommandsList()
	{
		return systemCommands.keySet().toString();
	}
	
	public SystemCommandProcessor getSystemCommand(String processName)
	{
		return systemCommands.get(processName);
	}
	
	/**
	 * @return Returns a list of the current commands within the known operation commands
	 */
	public String getOperationCommandsList()
	{
		return operationCommands.keySet().toString();
	}
	
	public OperationCommandProcessor getOperationCommand(String processName)
	{
		return operationCommands.get(processName);
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
	 * Will throw an exception if the log level cannot be set
	 */
	public void setLogLevel(CommandServiceMessage command) throws Exception
	{
		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process SetLogLevel command", "Info");
		
		this.logLevel = command.getParam("Explanation");
		
		this.logger.sendLogMessage("Success", "Successfully processed Exit Command", "Info");
	}
	
	/**
	 * Handles terminating the whole application with no clean-up; used if in case of critical errors
	 * 
	 * Will throw an exception if it cannot exit out of the application
	 */
	public void exit(CommandServiceMessage command) throws Exception
	{
		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Exit command", "Info");
		
		this.logger.sendLogMessage("Success", "Successfully processed Exit Command", "Info");
		
		Thread.sleep(5);
		
		System.exit(1);
	}
	
	/**
	 * Handles terminating an individual program with clean-up
	 * 
	 * Will throw an exception if the program cannot stop
	 */
	public void stop(CommandServiceMessage command) throws Exception
	{
		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Stop command", "Info");
		
		if(this.isRunning())
		{
			this.setRunning(false);
			
			this.logger.sendLogMessage("Success", "Successfully processed Stop command", "Info");
		}
		else
		{
			this.logger.sendLogMessage("Failure", "Unable to process Stop command", logLevel);
			
			command.addParam("Response", "Failure");
			command.addParam("Expalantion", this.toString() + " has already stopped");
		}
	}
	
	/**
	 * Handles starting a program if it has been stopped
	 * 
	 * Will throw an exception if it cannot start the program
	 */
	public void start(CommandServiceMessage command) throws Exception
	{
		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Start command", "Info");
		
		if(this.isRunning())
		{
			this.logger.sendLogMessage("Failure", "Unable to process Start command", logLevel);
			
			command.addParam("Response", "Failure");
			command.addParam("Explanation", this.toString() + " has already started");
		}
		else
		{
			this.setRunning(true);
			
			this.logger.sendLogMessage("Success", "Successfully processed Start command", "Info");
		}
	}
	
	/**
	 * Handles temporarily halting an individual process
	 * 
	 * Will throw an exception if the process cannot be paused
	 * 
	 * @param processName Identifier for the process
	 */
	public void pause(CommandServiceMessage command) throws Exception //TODO Need to fix
	{
		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Pause command", "Info");
	}
	
	/**
	 * Handles sending and returning a notification whether a program is running
	 * 
	 * Will throw an exception if it cannot retrieve the running state
	 */
	public void report(CommandServiceMessage command) throws Exception //Should this method still be able to send out a message to the logger even if the program has stopped?
	{
		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Report command", "Info");
	}
	/**
	 * Handles resuming a process after it has been paused
	 * 
	 * Will throw an exception if it cannot resume the process
	 * 
	 * @param processName Identifier for the process
	 */
	public void resume(CommandServiceMessage command) throws Exception //TODO Need to fix
	{
		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Resume command", "Info");
	}
	
	public void status(CommandServiceMessage command) throws Exception
	{
		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Status command", "Info");
	}
	
	public void doSomething(CommandServiceMessage command) throws Exception
	{
		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Do command", "Info");
	}

////////////////////////////*END OF METHODS USED BY COMMAND PROCESSORS*////////////////////////////
}