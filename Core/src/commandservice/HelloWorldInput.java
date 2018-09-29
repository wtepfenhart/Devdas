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
import java.util.HashMap;
import java.util.Scanner;

import devdas.Configuration;

public class HelloWorldInput extends DevdasCore
{

	Scanner scanner;
	

	public HelloWorldInput(Configuration config) {
		super(config);
		scanner = new Scanner(System.in);
	}

	/**
	 * Used for debugging and testing the class code
	 * 
	 * @param args - command line arguments
	 */
	public static void main(String[] args)
	{
		Configuration config = new Configuration(args);
		HelloWorldInput tester = new HelloWorldInput(config);
		tester.run();
	}
	

	public void initializeAgentCommands() {
		HashMap<String,AgentProcessor> agentCommands = new HashMap<String,AgentProcessor>();
	}

	@Override
	public ArrayList<String> initializeAgentTopics() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("All");
		return result;
	}

	@Override
	public void agentFunction() {
		System.out.print("Enter string to send: ");
		String msg = scanner.nextLine();
		AgentMessage a = new AgentMessage();
		a.setType("Command");
		a.addParam("Say",msg);
		a.setDestination("Say");
		sendAgentMessage(a.getDestination(),a);
	}
}
