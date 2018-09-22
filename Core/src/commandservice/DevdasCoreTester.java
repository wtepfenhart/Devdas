package commandservice;

import java.lang.Thread.State;
import java.util.Scanner;
import devdas.Configuration;
import devdas.LogSubscriber;

public class DevdasCoreTester extends DevdasCore
{
	public DevdasCoreTester(Configuration config, String pubExchange, String subExchange)
	{
		super(config, pubExchange, subExchange);
	}
	
	public DevdasCoreTester(Configuration config)
	{
		super(config);
	}
	
	public DevdasCoreTester(Configuration config, String[] commands, OperationCommandProcessor[] opProcessors)
	{
		super(config, commands, opProcessors);
	}
	
	/**
	 * Handles temporarily halting an individual process
	 * 
	 * Will throw an exception if the process cannot be paused
	 * 
	 * @param processName Identifier for the process
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void pause(CommandServiceMessage command) throws Exception //TODO Need to fix
	{
		super.pause(command);
		
		if(this.isRunning())
		{
			Thread sys = getSystemCommand(command.getParam("Explanation").toUpperCase()).getProcessingThread();
			Thread opt = getOperationCommand(command.getParam("Explanation").toUpperCase()).getProcessingThread();

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
				throw new Exception("Cannot pause " + command.getParam("Explanation")); //Cannot pause something that doesn't exist or that isn't already running
			}
		}
		else
		{
			throw new Exception(this.toString() + "is no longer running");
		}
		
		command.addParam("Response", "Success");
		sendLogMessage("Success", "Successfully processed Pause command", "Info");
	}
	
	/**
	 * Handles sending and returning a notification whether a program is running
	 * 
	 * Will throw an exception if it cannot retrieve the running state
	 */
	@Override
	public void report(CommandServiceMessage command) throws Exception //Should this method still be able to send out a message to the logger even if the program has stopped?
	{
		super.report(command);
		
		command.addParam("Response", "Success");
		command.addParam("Explanation", this.toString() + " is " + (this.isRunning() ? "RUNNING" : "NOT RUNNING")); //TODO Refactor "not running" to something more succinct
		sendLogMessage("Success", "Successfully processed Report command", "Info");
	}
	
	/**
	 * Handles resuming a process after it has been paused
	 * 
	 * Will throw an exception if it cannot resume the process
	 * 
	 * @param processName Identifier for the process
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void resume(CommandServiceMessage command) throws Exception //TODO Need to fix
	{
		super.resume(command);
		
		if(this.isRunning())
		{
			Thread sys = getSystemCommand(command.getParam("Explanation").toUpperCase()).getProcessingThread();
			Thread opt = getOperationCommand(command.getParam("Explanation").toUpperCase()).getProcessingThread();

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
				throw new Exception("Cannot resume " + command.getParam("Explanation")); //Cannot start something that doesn't exist or that is already running
			}
		}
		else
		{
			throw new Exception(this.toString() + "is no longer running");
		}
		
		command.addParam("Response", "Success");
		sendLogMessage("Success", "Successfully processed Resume command", "Info");
	}
	
	@Override
	public void status(CommandServiceMessage command) throws Exception
	{
		super.status(command);
		
		if(this.isRunning())
		{
			Thread sys = getSystemCommand(command.getParam("Explanation").toUpperCase()).getProcessingThread();
			Thread opt = getOperationCommand(command.getParam("Explanation").toUpperCase()).getProcessingThread();

			if(sys != null)
			{
				command.addParam("Explanation", sys.toString() + " is " + (sys.isAlive() ? "ALIVE" : sys.isInterrupted() ? "INTERRUPTED" : sys.isDaemon() ? "DAEMON" : "DEAD") + " and " + sys.getState()); //Should we log the status of the processor?
			}
			else if(opt != null)
			{
				command.addParam("Explanation", opt.toString() + " is " + (opt.isAlive() ? "ALIVE" : opt.isInterrupted() ? "INTERRUPTED" : opt.isDaemon() ? "DAEMON" : "DEAD") + " and " + opt.getState());
			}
			else
			{
				throw new Exception("Unexpected command: " + command.getParam("Explanation").toUpperCase());
			}
		}
		else
		{
			throw new Exception(this.toString() + "is no longer running");
		}
		
		command.addParam("Response", "Success");
		sendLogMessage("Success", "Successfully processed Status command", "Info");
	}
	
	@Override
	public void doSomething(CommandServiceMessage command) throws Exception
	{
		super.doSomething(command);
		
		System.err.println("DOING SOMETHING");
		Thread.sleep(10000);
		
		command.addParam("Response", "Success");
		sendLogMessage("Success", "Successfully processed Do command", "Info");
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
		DevdasCoreTester prog = new DevdasCoreTester(config, source, destination);
		DevdasCoreTester dummy = new DevdasCoreTester(config, source, destination); //Tests for accidental multiprogram execution
		
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
			System.out.println("\tSYSTEM COMMANDS: " + prog.getSystemCommandsList());
			System.out.println("\tOPERATION COMMANDS: " + prog.getOperationCommandsList());
		
		System.out.println("==============");
		
		//Test
		while(true)
		{
			//Initialize message command
			CommandServiceMessage commander = new CommandServiceMessage();
				commander.addParam("Source", userSub.toString());
				commander.addParam("Destination", prog.toString());
				System.out.println("\tCreated new commander: " + commander.toJSONString());
				
			System.out.println("--------------");
			
			//Ask for command
			System.out.print("Send a command: ");
				String command = keyboard.nextLine();
				System.out.println();
			
			//Set command and send
			if(command.contains(" ")) //Command has an option
			{
				commander.addParam("Source", command.substring(0, command.indexOf(" ")));
				commander.addParam("Destination", command.substring(command.indexOf(" ") + 1));
			}
			else
			{
				commander.addParam("Command", command);
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
