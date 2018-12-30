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
				
				InterestInterpreter i = new InterestInterpreter(cmd.getSource(), cmd.getParam("Interests"));
				addKeyToInterests(cmd.getSource(), i);
				
				announce();
				
				//Testing KeyToInterests methods
/*
				InterestInterpreter dummy = new InterestInterpreter("DUMMY", "BLANK");
				System.err.println(modifyKeyToInterests(i, dummy));
				
				announce();
				
				InterestInterpreter dummy2 = new InterestInterpreter("NEW_DUMMY", "BLANK");
				System.err.println(addKeyToInterests("NEW_DUMMY", dummy2));
				
				announce();
				
				System.err.println(removeKeyToInterests(dummy));
				
				announce();
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
	
	public boolean addKeyToInterests(String agent, InterestInterpreter keyToInterest)
	{
		if(this.keyToInterests.contains(keyToInterest)) //Checks to see if there is an InterestInterpreter already assigned to the agent
		{
			return this.keyToInterests.add(keyToInterest);
		}
		else
		{
			boolean isModified = this.keyToInterests.add(keyToInterest);
			this.agentReactions.put("ContextFreeText", keyToInterests);
			
			return isModified;
		}
	}
	
	//TODO Hard to implement unless you have access to old key; override equals() method?
	public boolean removeKeyToInterests(InterestInterpreter keyToInterest)
	{
		return this.keyToInterests.remove(keyToInterest);
	}
	
	//TODO Hard to implement unless you have access to old key; override equals() method?
	public boolean modifyKeyToInterests(InterestInterpreter oldKey, InterestInterpreter newKey)
	{
		if(keyToInterests.contains(oldKey)) //Prevents IndexOutOfBoundsException on .set()
		{
			this.keyToInterests.set(keyToInterests.indexOf(oldKey), newKey);
			return true;
		}
		
		return false;
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