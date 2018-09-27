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

	public HashMap<String, AgentProcessor> initializeAgentCommands() {
		// TODO Auto-generated method stub
		HashMap<String,AgentProcessor> result = new HashMap<String,AgentProcessor>();
		result.put("Test", new TestService(this));
		return result;
	}

	@Override
	public ArrayList<String> initializeAgentTopics() {
		// TODO Auto-generated method stub
		ArrayList<String> result = new ArrayList<String>();
		result.add("All");
		return result;
	}

	@Override
	public void processAgentMessage(AgentMessage msg) {
		// TODO Auto-generated method stub
	}

	public void test(AgentMessage msg) {
		System.out.println("It worked");
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
