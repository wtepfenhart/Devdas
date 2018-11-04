package textToIntention;

import java.util.ArrayList;
import java.util.Map;
import commandservice.AgentMessage;
import commandservice.AgentReaction;
import commandservice.DevdasCore;
import devdas.Configuration;

/**
 * A {@code TextToIntention} Agent serves to interpret raw, context-free text into commands that will be sent out to other Agents.
 * The text will be interpreted by a specific set of keys, where these keys each have their own set of keywords.
 * A keyword will be matched via the {@code isInterested(String)} method of an {@link InterestInterpreter}
 * 
 * @author B-T-Johnson
 */
public class TextToIntention extends DevdasCore
{
	private ArrayList<InterestInterpreter> keyToInterests;
	
	public TextToIntention(Configuration config)
	{
		super(config);
	}
	
	/**
	 * Helps construct all {@link InterestInterpreter}s needed by the agent
	 */
	public class Initializer implements AgentReaction
	{
		/**
		 * Default Constructor
		 */
		public Initializer()
		{}

		/**
		 * Assigns interest(s) to agentReactions
		 */
		public void execute(AgentMessage cmd)
		{	
			if(cmd.getTopic().equals("Announcement") && cmd.getInterest().equals("Interests") && (cmd.getDestination().isEmpty() || cmd.getDestination() == null))
			{
				for(Map.Entry<String, String> map : cmd.getParams()) //TODO Modify .getParams() to return one or more Strings; may have multiple keywords
				{
					addKeyToInterests(map.getKey(), new InterestInterpreter(map.getValue()));
				}
			}
		}
	}
	
	public void initializeAgentReactions()
	{
		agentInterests.add("Announcement");
		agentInterests.add("ContextFreeText");
		agentReactions.put("Interests", new Initializer());
	}
	
	public void agentActivity()
	{
		//TODO Send context as replyTo
		try
		{
			Thread.sleep(10);
		}
		catch(InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void announce()
	{
		System.out.println(agentReactions.values());
	}
	
	public void addKeyToInterests(String interestName, InterestInterpreter...keyToInterests)
	{
		for(InterestInterpreter key : keyToInterests)
		{
			if (!this.keyToInterests.contains(key))
			{
				this.keyToInterests.add(key);
				agentReactions.put(interestName, key);
			}
			else
			{
				modifyKeyToInterests(interestName, key);
			}
		}
	}
	
	public void removeKeyToInterests(InterestInterpreter...keyToInterests)
	{
		for(InterestInterpreter key : keyToInterests)
		{
			if (!this.keyToInterests.contains(key))
			{
				if(this.keyToInterests.size() > 1)
				{
					this.keyToInterests.remove(key);
					agentReactions.remove("Interests", key);
				}
			}
			else
			{
				//Should we log an error?
			}
		}
	}
	
	public void modifyKeyToInterests(String target, InterestInterpreter newKey)
	{
		agentReactions.replace("Interests", agentReactions.get(target), newKey);
	}

	/**
	 * Used for debugging and testing the class code
	 * 
	 * @param args - command line arguments
	 */
	// TODO replace with junit testing
	public static void main(String[] args) //TODO ERROR; something wrong with build-path (cannot find user.ini)
	{
		Configuration config = new Configuration(args);
		TextToIntention tester = new TextToIntention(config);
		tester.run();
	}
}