package commandservice;

import java.util.ArrayList;
import java.util.HashMap;

import devdas.Configuration;

public class DevdasCoreTester extends DevdasCore
{
	
	public DevdasCoreTester(Configuration config) {
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
		DevdasCoreTester tester = new DevdasCoreTester(config);
		tester.run();
	}

	public void initializeAgentReactions()
	{
		agentInterests.add("All");
		agentInterests.add("Command");
		
		HashMap<String,AgentReaction> result = new HashMap<String,AgentReaction>();
		result.put("Say", new TestService(this));
		agentReactions = result;
	}

	public void test(AgentMessage msg) {
		String s = ((ArrayList<String>) msg.getParam("Say")).get(0);
		System.out.println(s);
	}

	@Override
	public void agentActivity() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
