/**
 *
 * @file HelloWorldOutput.java
 * @author wtepfenhart
 * @date: Sep 29, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */

package commandservice;

import java.util.ArrayList;

import devdas.Configuration;

public class HelloWorldOutput extends DevdasCore
{
	/**
	 * This is the constructor for the HelloWorldOutput class
	 * @param config
	 */
	public HelloWorldOutput(Configuration config) {
		super(config);
	}

	/**
	 * Used for debugging and testing the class code
	 * 
	 * @param args - command line arguments
	 */
	public static void main(String[] args)
	{
		Configuration config = new Configuration(args);
		HelloWorldOutput tester = new HelloWorldOutput(config);
		tester.run();
	}

	/**
	 * This is a class that responds where there is a message with
	 * with Route Say posted in RabbitMQ
	 * @author wtepfenhart
	 *
	 */
	public class Say implements AgentReaction{

		/**
		 * Default Constructor
		 */
		public Say() {

		}

		/**
		 * Simple print line to a console
		 */
		public void execute(AgentMessage cmd) {
			System.out.println(cmd.getParam("Subject"));
		}
	}

	/**
	 * Initializes the agent to look for messages from the "Say" route
	 */
	public void initializeAgentReactions()
	{
		agentInterests.add("Announcement");
		Say s = new Say();
		agentReactions.put("Announcement", new ArrayList<AgentReaction>(){{add(s);}});
	}

	/**
	 * This is a do nothing method that sleeps for 10 milliseconds at a time
	 */
	public void agentActivity() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
