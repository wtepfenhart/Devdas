package textToIntention;

import java.util.HashMap;
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
	private HashMap<String, InterestInterpreter> keyToInterests = new HashMap<>();
	
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
				//System.err.println("Received " + cmd);
				//System.out.println(cmd.getParam("Interests"));
				
				addKeyToInterests(cmd.getSource(), new InterestInterpreter(cmd.getParam("Interests")));
				
				//announce();
			}
		}
	}
	
	public void initializeAgentReactions()
	{
		agentInterests.add("Announcement");
		agentReactions.put("Announcement", new Initializer());
		
		agentInterests.add("ContextFreeText");
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
	
	public void announce() //May need to move up to Core
	{
		System.out.println(agentReactions);
		System.out.println(keyToInterests);
	}
	
	//TODO Make these methods access InterestInterpreters by their respective key
	public void addKeyToInterests(String agent, InterestInterpreter...keyToInterests)
	{
		for(InterestInterpreter key : keyToInterests)
		{
			this.keyToInterests.put(agent, key);
			agentReactions.put("ContextFreeText", key);
		}
	}
	
	public void removeKeyToInterests(String agent, InterestInterpreter...keyToInterests)
	{
		for(InterestInterpreter key : keyToInterests)
		{
			if (this.keyToInterests.containsValue(key))
			{
				if(this.keyToInterests.size() > 1)
				{
					this.keyToInterests.remove(agent, key);
					agentReactions.remove("ContextFreeText", key);
				}
			}
		}
	}
	
	public void modifyKeyToInterests(String target, InterestInterpreter newKey)
	{
		agentReactions.replace("ContextFreeText", agentReactions.get(target), newKey);
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