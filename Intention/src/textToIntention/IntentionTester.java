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
	private Scanner scanner;
	private boolean notDone = true;
	
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
		if(notDone)
		{
			String[] keywords = {"Apple","Orange","Pineapple"};
			AgentMessage a = new AgentMessage();
			a.addParam("Interests", keywords);
			a.setTopic("Announcement");
			a.setInterest("Interests");
			sendAgentMessage(a.getRoute(),a);
			
			notDone = false;
		}
		
		System.out.print("Enter string to send: ");
		String msg = scanner.nextLine();
		AgentMessage b = new AgentMessage();
		b.addParam("Text", msg);
		b.setTopic("ContextFreeText");
		sendAgentMessage(b.getRoute(), b);
	}
	
	public static void main(String[] args)
	{
		Configuration config = new Configuration(args);
		IntentionTester tester = new IntentionTester(config);
		tester.run();
	}
}