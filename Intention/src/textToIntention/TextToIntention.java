package textToIntention;

import java.util.HashMap;
import commandservice.AgentMessage;
import commandservice.AgentReaction;
import commandservice.DevdasCore;
import devdas.Configuration; 

/**
 * A {@code TextToIntention} Agent serves to interpret raw, context-free text into commands that will be sent out to other Agents.
 * The text will be interpreted by a specific set of keys, where these keys each have their own set of keywords.
 * A keyword will be matched via the {@code isInterested(String)} method of an {@link InterestInterpreter}.
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
	private class Initializer implements AgentReaction
	{
		/**
		 * Default Constructor
		 */
		private Initializer()
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
				
				addKeyToInterests(cmd.getSource(), new InterestInterpreter(cmd.getSource(), cmd.getParam("Interests")));
				
				
				//Testing KeyToInterests methods
/*				announce();
				
				InterestInterpreter dummy = new InterestInterpreter("DUMMY", "BLANK");
				modifyKeyToInterests(cmd.getSource(), dummy);
				
				announce();
				
				InterestInterpreter dummy2 = new InterestInterpreter("NEW_DUMMY", "BLANK");
				addKeyToInterests("NEW_DUMMY", dummy2);
				
				announce();
				
				removeKeyToInterests(cmd.getSource(), dummy);
				
				announce();
*/
			}
		}
	}

	/**
	 * Reads in context-free text from an {@link AgentMessage} and produces the required context-reliant text for the particular Agent
	 */
	private class Translator implements AgentReaction //Is there a better name for this?
	{
		/**
		 * Default constructor
		 */
		private Translator()
		{}
		
		@Override
		public void execute(AgentMessage command)
		{
			// TODO Auto-generated method stub
		}
	}
	
	public void initializeAgentReactions()
	{
		agentInterests.add("Announcement");
		agentReactions.put("Announcement", new Initializer());
		
		agentInterests.add("ContextFreeText");
		//TODO Add reaction to ContextFreeText; ContextFreeTextToContextReliantText/Translator class?
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
	
	public void announce() //TODO May need to move up to Core as systemCommand method
	{
		System.out.println(agentReactions);
		System.out.println(keyToInterests);
	}
	
	public void addKeyToInterests(String agent, InterestInterpreter keyToInterest)
	{
		if(!this.keyToInterests.containsValue(keyToInterest))
		{
			this.keyToInterests.put(agent, keyToInterest);
			agentReactions.put("ContextFreeText", keyToInterest);
		}
		else
		{
			this.keyToInterests.get(agent).addKeyword(keyToInterest.getKeywords()); //TODO Have not tested
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
	
	@Deprecated
	public void modifyKeyToInterests(String target, InterestInterpreter...newKey)
	{
		for(InterestInterpreter key : newKey)
		{
			this.keyToInterests.replace(target, keyToInterests.get(target), key);
			agentReactions.replace("ContextFreeText", agentReactions.get(target), key);
		}
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