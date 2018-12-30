package textToIntention;

import java.util.ArrayList;
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
	private static Scanner scanner = new Scanner(System.in);
	private boolean notDone = true;
	private String[] keywords;
	
	public IntentionTester(Configuration config, String... keywords)
	{
		super(config);
		this.keywords = keywords;
	}

	@Override
	public void initializeAgentReactions()
	{}

	@Override
	public void agentActivity()
	{
		//Mimics an agent creating its interests
		if(notDone)
		{
			AgentMessage a = new AgentMessage();
			a.addParam("Interests", keywords);
			a.setTopic("Announcement");
			a.setInterest("Interests");
			sendAgentMessage(a.getRoute(), a);
			
			notDone = false;
		}
		
		//Mimics another agent sending RawTextCommands
		System.out.print("Enter string to send: ");
		String msg = scanner.nextLine();
		AgentMessage b = new AgentMessage();
		b.addParam("Text", msg);
		b.setTopic("ContextFreeText");
		b.setInterest(b.getSource()); //Ensures only one InterestInterpreter is executing at a time; the message is being directed to itself
		sendAgentMessage(b.getRoute(), b);
	}
	
	public static void main(String[] args)
	{
		Configuration config = new Configuration(args);
		ArrayList<String> keywords = new ArrayList<String>();
		
		System.out.println("===== YOU ARE ALLOWED A MAXIMUM OF 10 KEYWORDS =====");
		
		for(int i = 0; i < 10; i++)
		{
			System.out.print("Enter a keyword to target (-1 to stop): ");
			String response = scanner.nextLine();
			
			if(response.equals("-1"))
			{
				break;
			}
			else
			{
				keywords.add(response);
			}
		}
		System.out.println("====================================================");
		
		IntentionTester tester = new IntentionTester(config, keywords.toArray(new String[keywords.size()]));
		tester.run();
	}
}