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

import devdas.Configuration;

public class HelloWorldOutput extends DevdasCore
{
	
	public HelloWorldOutput(Configuration config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Used for debugging and testing the class code
	 * 
	 * @param args - command line arguments
	 */
	// TODO replace with junit testing
	public static void main(String[] args)
	{
		Configuration config = new Configuration(args);
		HelloWorldOutput tester = new HelloWorldOutput(config);
		tester.run();
	}
	
	public class Say implements AgentProcessor{
		
		public void Say() {
			
		}
		
		public void execute(AgentMessage cmd) {
			System.out.println(cmd.getParam("Say"));
		}
	}

	public void initializeAgentCommands() {
		// TODO Auto-generated method stub
		HashMap<String,AgentProcessor> result = new HashMap<String,AgentProcessor>();
		result.put("Say", new Say());
		agentCommands = result;
	}

	@Override
	public ArrayList<String> initializeAgentTopics() {
		// TODO Auto-generated method stub
		ArrayList<String> result = new ArrayList<String>();
		result.add("All");
		result.add("Say");
		return result;
	}

	@Override
	public void agentFunction() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
