package textToIntention;

import java.util.Scanner;

import commandservice.AgentMessage;
import commandservice.DevdasCore;
import devdas.Configuration;

/**
 * Testing class for {@link TextToIntention} functionality. Used to mimic the behavior of a potential Agent.
 * 
 * @author B-T-Johnson
 */
public class IntentionTester extends DevdasCore
{
	Scanner scanner;
	private int i = 0;
	
	public IntentionTester(Configuration config)
	{
		super(config);
		scanner = new Scanner(System.in);
	}

	@Override
	public void initializeAgentReactions()
	{}

	@Override
	public void agentActivity()
	{	
		if(i == 0)
		{
			String[] keywords = {"Apple","Orange","Pineapple"};
			AgentMessage a = new AgentMessage();
			a.addParam("Interests", keywords);
			a.setTopic("Announcement");
			a.setInterest("Interests");
			sendAgentMessage(a.getRoute(),a);
			
			i = 1;
		}
		
		System.out.print("Enter string to send: ");
		String msg = scanner.nextLine();
		AgentMessage a = new AgentMessage();
		a.addParam("Text",msg);
		a.setTopic("ContextFreeText");
		sendAgentMessage(a.getRoute(),a);
	}
	
	public static void main(String[] args)
	{
		Configuration config = new Configuration(args);
		IntentionTester tester = new IntentionTester(config);
		tester.run();
	}
}