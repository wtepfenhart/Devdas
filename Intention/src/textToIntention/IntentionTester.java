package textToIntention;

import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

import commandservice.AgentMessage;
import commandservice.AgentReaction;
import commandservice.DevdasCore;
import devdas.Configuration;

/**
 * Testing class for {@link TextToIntention} functionality. Used to mimic the dialogue between the TextToIntention Agent and a potential Agent.
 * 
 * @author B-T-Johnson
 */
public class IntentionTester extends DevdasCore
{
	private static Scanner scanner = new Scanner(System.in);
	private boolean notDone = true;
	private String[] keywords;
	
	public IntentionTester(Configuration config, Collection<? extends String> keywords)
	{
		super(config);
		this.keywords = keywords.toArray(new String[keywords.size()]);
	}
	
	public IntentionTester(Configuration config, String... keywords)
	{
		this(config, Arrays.asList(keywords));
	}
	
	private class Responder implements AgentReaction
	{
		public Responder()
		{}
		
		public void execute(AgentMessage command)
		{
			System.err.printf("Received response: %.2f%% MATCH%n", Double.parseDouble(command.getParam("Match", 0)));
		}
	}

	@SuppressWarnings("serial")
	public void initializeAgentReactions()
	{
		this.agentInterests.add("Match");
		
		this.agentReactions.put("Match", new ArrayList<AgentReaction>(){{add(new Responder());}});
	}

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
		sendAgentMessage(b.getRoute(), b);
	}
	
	public static void main(String[] args)
	{
		Configuration config = new Configuration(args);
		HashSet<String> keywords = new HashSet<String>();
		
		System.out.println("===== YOU ARE ALLOWED A MAXIMUM OF 10 UNIQUE KEYWORDS =====");
		
		while(keywords.size() < 10)
		{
			System.out.print("Enter a keyword to target (-1 to stop): ");
			String response = scanner.nextLine();
			
			if(response.equals("-1"))
			{
				if(keywords.size() == 0)
				{
					System.err.println("You must enter at least one keyword");
				}
				else
				{	
					break;
				}
			}
			else if(response.equals("") || response == null)
			{
				System.err.println("Sorry, didn't get that");
			}
			else
			{
				keywords.add(response);
			}
		}
		System.out.println("====================================================");
		
		IntentionTester tester = new IntentionTester(config, keywords);
		tester.run();
	}
}