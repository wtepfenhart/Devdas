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

	public void initializeAgentCommands() {
		// TODO Auto-generated method stub
		HashMap<String,AgentProcessor> result = new HashMap<String,AgentProcessor>();
		result.put("Say", new TestService(this));
		agentCommands = result;
	}

	@Override
	public ArrayList<String> initializeAgentTopics() {
		// TODO Auto-generated method stub
		ArrayList<String> result = new ArrayList<String>();
		result.add("All");
		result.add("Command");
		return result;
	}

	public void test(AgentMessage msg) {
		String s = msg.getParam("Say");
		System.out.println("s");
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
