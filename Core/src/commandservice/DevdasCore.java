package commandservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import devdas.Configuration;
import devdas.LogPublisher;

/**
 * Generic program that processes commands received by a CommandServiceSubscriber and sends the results through a CommandServicePublisher.
 * The results of a command may be a simple display of a variable's value or another command itself. 
 * 
 * @author B-T-Johnson
 */
public abstract class DevdasCore
{
	private String hostID;
	private Configuration configuration; 

	private boolean agentRunning = false;
	private boolean commandRunning = false;
	private boolean errorState = false; //When will this condition be required? Upon a failed execution? How do we know when an execution has failed? When there's an exception thrown, or when we consider its response invalid?

	private LogPublisher logger; 
	private CommandServicePublisher commandPublisher; 
	private AgentServicePublisher agentPublisher; 

	private ArrayList<String> systemRoutes;
	protected ArrayList<String> agentInterests;

	private Map<String, CommandProcessor> systemCommands;
	protected Map<String, ArrayList<? extends AgentReaction>> agentReactions; 

	private String logLevel;

	/**
	 * This allocates a HashMap for the agent commands and initializes the topics which this agent will register. In the background,
	 * additional topics will be registered such as the UUID of the executing agent.
	 */
	public abstract void initializeAgentReactions();

	public abstract void agentActivity();
	
	/**
	 * Creates a new Generic Program set by the Configuration object to define its Publisher and Subscriber
	 * 
	 * @param config Configuration object to set
	 */
	public DevdasCore(Configuration config)
	{
		// initialize key system data
		configuration = config;
		hostID = (UUID.randomUUID()).toString();

		//have to initialize the class variables 
		CommandMessage.hostID = hostID;
		AgentMessage.hostID = hostID;
		
		//set up logging
		logger = new LogPublisher(config, config.getLogExchange());
		logger.setLevel("System");
		logger.start();
		logger.sendLogMessage("Start up", hostID + " started", "System");

		//set up system command functionality
		systemCommands = new HashMap<String, CommandProcessor>();
		systemRoutes = new ArrayList<String>();
		systemRoutes.add(hostID);
		systemRoutes.add("All");
		initializeSystemCommands();
		commandPublisher = new CommandServicePublisher(configuration,configuration.getSystemExchange());
		commandPublisher.start();
		recieveCommandMessages(configuration.getSystemExchange(),systemRoutes);
		
		// setup agent command functionality
		agentReactions = new HashMap<String, ArrayList<? extends AgentReaction>>();
		agentInterests = new ArrayList<String>();
		
		initializeAgentReactions();
		agentInterests.add("All");
		agentInterests.add(hostID);
		
		//Set up publisher/subscriber and start
		agentPublisher = new AgentServicePublisher(configuration,configuration.getAgentExchange());
		agentPublisher.start();
		recieveAgentMessages(configuration.getAgentExchange(),agentInterests);
		commandRunning = true;
		agentRunning = true;
	}

	/**
	 * This initializes all of the commands.
	 */
	private void initializeSystemCommands() {
		//Initialize systemCommands
		systemCommands.put("Start", new StartCommand(this)); 
		systemCommands.put("Stop", new StopCommand(this));
		systemCommands.put("Exit", new ExitCommand(this)); 
		systemCommands.put("Pause", new PauseCommand(this)); 
		systemCommands.put("Resume", new ResumeCommand(this)); 
		systemCommands.put("LogLevel", new SetLogLevelCommand(this)); 
		systemCommands.put("Status", new StatusCommand(this)); 
		systemCommands.put("Report", new ReportCommand(this)); 
	}


	/**
	 * This initializes the ability of the program to receive system commands. Handling the command
	 * is accomplished by calling the processCommand method.
	 * 
	 * @param exch -the exchange for publish/subscribe 
	 * @param systemRoutes - all of the various keywords for messages that it will receive
	 */
	public void recieveCommandMessages(String exch,ArrayList<String> routes)
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(configuration.getIpAddress());
		factory.setUsername(configuration.getUserName());
		factory.setPassword(configuration.getUserPassword());
		factory.setVirtualHost(configuration.getVirtualHost());
		try
		{
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.exchangeDeclare(exch, "direct");
			String queueName = channel.queueDeclare().getQueue();
			for (String rt : routes)
			{
				channel.queueBind(queueName, exch, rt);
			}

			System.out.println(" [" + exch + "]" + "\tWaiting for messages.");

			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope,
						AMQP.BasicProperties properties, byte[] body) throws IOException
				{
					String message = new String(body, "UTF-8");
					JSONParser parser = new JSONParser();
					JSONObject json;
					try
					{
						json = (JSONObject) parser.parse(message);
						CommandMessage msg = new CommandMessage(json);
						processSystemCommand(msg);
					}
					catch (ParseException e)
					{
						e.printStackTrace();
					}
				}
			};
			channel.basicConsume(queueName, true, consumer);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
		}
	}

	/**
	 * This initializes the ability of the program to recieve system commands. Handling the command
	 * is accomplished by calling the processCommand method.
	 * 
	 * @param exch -the exchange for publish/subscribe 
	 * @param systemRoutes - all of the various keywords for messages that it will recieve
	 */
	public void recieveAgentMessages(String exch,ArrayList<String> routes) {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(configuration.getIpAddress());
		factory.setUsername(configuration.getUserName());
		factory.setPassword(configuration.getUserPassword());
		factory.setVirtualHost(configuration.getVirtualHost());
		try {
			Connection connection = factory.newConnection();
			Channel channel = connection.createChannel();

			channel.exchangeDeclare(exch, "direct");
			String queueName = channel.queueDeclare().getQueue();
			for (String rt : routes)
			{
				channel.queueBind(queueName, exch, rt);
			}

			System.out.println(" [" + exch + "]" + "\tWaiting for messages.");

			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope,
						AMQP.BasicProperties properties, byte[] body) throws IOException
				{
					String message = new String(body, "UTF-8");
					JSONParser parser = new JSONParser();
					JSONObject json;
					try
					{
						json = (JSONObject) parser.parse(message);
						AgentMessage msg = new AgentMessage(json);
						processAgentMessage(msg);
					}
					catch (ParseException e)
					{
						e.printStackTrace();
					}
				}
			};
			channel.basicConsume(queueName, true, consumer);
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace(System.out);
		}
	}

	public void processAgentMessage(AgentMessage msg)
	{
		String cmd = msg.getTopic();
		if (cmd != null && !cmd.isEmpty())
		{
			ArrayList<? extends AgentReaction> s = agentReactions.get(cmd); //get the command processor for the command
			if (s!=null)
			{
				for(AgentReaction e: s)
					e.execute(msg);  //use it
			}
		}
	}

	/**
	 * Determines the type of command message that was received and forwards it to the appropriate
	 * handler for that type. 
	 * 
	 * @param msg the CommandService message that contains the command
	 */
	private void processSystemCommand(CommandMessage msg) 
	{
		switch(msg.getType())
		{
		case "Command":
			handleSystemCommand(msg);
			break;
		case "Broadcast":
			handleSystemBroadcast(msg);
			break;
		case "Response":
			handleSystemResponse(msg);
			break;
		default:
			logger.sendLogMessage("Cmd Msg Error", "Unknown message type: " + msg.getType(), "System");
			break;		
		}
	}
	
	
/**
 * This handles messages that are responses to previous commands sent out by the agent. However,
 * an agent won't be sending commands that demand a reply to other agents or to the system until
 * a mechanism is set up for direct agent to agent collaborations. Even so, that should be a agent level
 * communications issue.
 * 
 * @param msg
 */
	private void handleSystemResponse(CommandMessage msg)
	{
		// this is in response to a system command sent out previously
		// agents shouldn't be doing system command kind of stuff so log it and do nothing
		
		logger.sendLogMessage("Messaging Error", "Recieved unexpected response message", "System");
	}

	/**
	 * This handles the case where something has sent out a broadcast for consumption by the agetns. This might
	 * be a command to be executed by all agents, such as a status command. For now it will be assumed that it is
	 * a command. In the future it might be some kind of system setting that gets passed around to the agents.
	 * 
	 * @param msg
	 */
	private void handleSystemBroadcast(CommandMessage msg) {
		// check to see if it is actually command that's been broadcast to all agents
		String cmd = msg.getParam("command");
		
		if (cmd != null && !cmd.isEmpty())
		{
			handleSystemCommand(msg);
			return;
		}
		
		// some other kind of broadcast?? maybe??
	}
		
	/**
	 * This invokes the command processor requested within the command message
	 * @param msg
	 */
	private void handleSystemCommand(CommandMessage msg) {
		String cmd = msg.getParam("command");
		
		// Check to see if there's a command provided in the message
		if (cmd != null && !cmd.isEmpty())
		{
			CommandProcessor s = systemCommands.get(cmd); //get the command processor for the command
			
			if (s!=null)
			{
				s.execute(msg);  //use it
			}
		}
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

		commandRunning = running;
	}

	/**
	 * @return Returns whether the program thread is running
	 */
	public boolean isRunning()
	{
		return this.commandRunning;
	}

	/**
	 * @return Returns a list of the current commands within the known system commands
	 */
	public String getSystemCommandsList()
	{
		return systemCommands.keySet().toString();
	}

	public CommandProcessor getSystemCommand(String processName)
	{
		return systemCommands.get(processName);
	}

	/**
	 * @return Returns a list of the current topics that is known by its agent(s)
	 */
	public String getAgentInterestsList()
	{
		return agentInterests.toString();
	}

	
	/**
	 * Returns an array of the AgentReactions assigned to the given topic
	 * 
	 * @param topic Key that refers to a subset of AgentReactions
	 * @return An array of the AgentReactions assigned to the topic
	 */
	public AgentReaction[] getAgentReactions(String topic)
	{
		try
		{
			return agentReactions.get(topic).toArray(new AgentReaction[agentReactions.get(topic).size()]);
		}
		catch(NullPointerException e)
		{
			return null;
		}
	}

	/**
	 * Sends a command through the publisher associated with this program as a CommandMessage
	 * 
	 * @param cmd The CommandMessage to send
	 */
	public void sendSystemMessage(CommandMessage cmd)
	{
		commandPublisher.setMessage(cmd);
	}
	
	public void sendAgentMessage(String rt, AgentMessage cmd)
	{
		agentPublisher.setMessage(rt, cmd.toString());
	}

	public void run() 
	{
		while (true)
		{
			while (commandRunning)
			{
				while (agentRunning)
				{
					agentActivity();
				}
				
				try 
				{
					Thread.sleep(10);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			try 
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public String getHostID() //Why not a systemCommand? Or is it because a Logger is not an instance of DevdasCore?
	{
		return this.hostID;
	}

	////////////////////////////*METHODS USED BY COMMAND PROCESSORS*////////////////////////////
	/**
	 * Handles setting a log level for the logger that is implemented in this class
	 */
	public void setLogLevel(CommandMessage command)
	{
		logLevel = command.getParam("logLevel");
		
		if (!logLevel.isEmpty() )
		{
			logger.setLevel(logLevel);
		}
	}

	
	
	/**
	 * Handles terminating the whole application with no clean-up; used if in case of critical errors
	 */
	public void exit(CommandMessage command) 
	{
		//		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Exit command", "Info");

		//		this.logger.sendLogMessage("Success", "Successfully processed Exit Command", "Info");

		try
		{
			Thread.sleep(5);
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		System.exit(1);
	}
	
	/**
	 * Handles stopping a program from operating
	 */
	public void stop(CommandMessage command)
	{
		//		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Stop command", "Info");

		commandRunning = false;
	}
	
	/**
	 * Handles starting a program if it has been stopped
	 */
	public void start(CommandMessage command)
	{
		//		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Start command", "Info");

		commandRunning = true;
	}
	
	/**
	 * Handles temporarily halting an individual process
	 * 
	 * @param processName Identifier for the process
	 */
	public void pause(CommandMessage command)
	{
		agentRunning = false;
		//		this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Pause command", "Info");
	}

	
	
	/**
	 * Handles sending and returning a notification whether a program is running
	 * 
	 * Will throw an exception if it cannot retrieve the running state
	 */
	public void report(CommandMessage command)
	{
		//this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Report command", "Info");
	}
	
	
	/**
	 * Handles resuming a process after it has been paused
	 * 
	 * Will throw an exception if it cannot resume the process
	 * 
	 * @param processName Identifier for the process
	 */
	public void resume(CommandMessage command)
	{
		agentRunning = true;
		//this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Resume command", "Info");
	}

	public void status(CommandMessage command)
	{
		//this.logger.sendLogMessage("Attempt", this.toString() + " attempting to process Status command", "Info");
	}
	
	public void announce(CommandMessage command)
	{
		System.out.println(agentReactions);
	}

	////////////////////////////*END OF METHODS USED BY COMMAND PROCESSORS*////////////////////////////
}