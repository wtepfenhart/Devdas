package textToIntention;

import java.util.ArrayList;
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
	private ArrayList<AgentReaction> keyToInterests = new ArrayList<AgentReaction>();
	
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
				System.err.println("Received InterestCommand " + cmd);
				//System.out.println(cmd.getParam("Interests"));
				
				addKeyToInterests(cmd.getSource(), new InterestInterpreter(cmd.getSource(), cmd.getParam("Interests")));
				
				announce();
				
				//Testing KeyToInterests methods
/*				
 * 				announce();
 *				
 *				InterestInterpreter dummy = new InterestInterpreter("DUMMY", "BLANK");
 *				modifyKeyToInterests(cmd.getSource(), dummy);
 *				
 *				announce();
 *				
 *				InterestInterpreter dummy2 = new InterestInterpreter("NEW_DUMMY", "BLANK");
 *				addKeyToInterests("NEW_DUMMY", dummy2);
 *				
 *				announce();
 *				
 *				removeKeyToInterests(cmd.getSource(), dummy);
 *				
 *				announce();
 */
			}
		}
	}
	
	public void initializeAgentReactions()
	{
		agentInterests.add("Announcement");
		agentInterests.add("ContextFreeText");
		
		agentReactions.put("Announcement", new ArrayList<AgentReaction>(){{add(new Initializer());}});
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
		if(!this.keyToInterests.contains(keyToInterest))
		{
			keyToInterests.add(keyToInterest);
			agentReactions.put("ContextFreeText", keyToInterests);
		}
		else
		{
			this.agentReactions.get("ContextFreeText").add(keyToInterest);
		}
	}
/*	
	public void removeKeyToInterests(String agent, InterestInterpreter...keyToInterests)
	{
		for(InterestInterpreter key : keyToInterests)
		{
			if (this.keyToInterests.contains(agent))
			{
				if(this.keyToInterests.size() > 1)
				{
					this.keyToInterests.removeAll(c)(agent, key);
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
			this.keyToInterests.replace(target, keyToInterests.get(target), (ArrayList<String>) key.getKeywords());
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