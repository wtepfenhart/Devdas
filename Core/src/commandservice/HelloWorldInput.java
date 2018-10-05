/**
 *
 * @file HelloWorldOutput.java
 * @author wtepfenhart
 * @date: Sep 29, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */

package commandservice;

import java.util.Scanner;
import devdas.Configuration;

/**
 * This is a simple Hello World program for the Devdas System and is to b3e used
 * as part of a tutorial.
 * @author wtepfenhart
 *
 */
public class HelloWorldInput extends DevdasCore
{

	Scanner scanner;
	

	public HelloWorldInput(Configuration config) {
		super(config);
		scanner = new Scanner(System.in);
	}
	
/**
 * This allocates a Hashmap for the agent commands. In this example there aren't any, since
 * all functionality is provided in the agentFunction method.
 */
	public void initializeAgentReactions() {
	}

	/**
	 * This initializes the topics which this agent will register. In the background,
	 * additional topics will be registered such as the UUID of the executing agent.
	 */
	@Override
	public void initializeAgentInterests() {
	}

	/**
	 * This is the method that defines the main function of the agent. It will be called repeatedly
	 * by the DevdasCore run method.
	 */
	public void agentActivity() {
		System.out.print("Enter string to send: ");
		String msg = scanner.nextLine();
		AgentMessage a = new AgentMessage();
		a.addParam("Subject",msg);
		a.setTopic("Announcement");
		sendAgentMessage(a.getRoute(),a);
	}
	

	/**
	 * This is the main for the program
	 * 
	 * @param args - command line arguments
	 */
	public static void main(String[] args)
	{
		Configuration config = new Configuration(args);
		HelloWorldInput tester = new HelloWorldInput(config);
		tester.run();
	}
}
