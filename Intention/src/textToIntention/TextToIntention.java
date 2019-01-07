package textToIntention;

import java.util.ArrayList;

import commandservice.AgentMessage;
import commandservice.AgentReaction;
import commandservice.DevdasCore;
import devdas.Configuration; 

/**
 * A {@code TextToIntention} Agent serves to interpret raw, context-free text into commands to be sent out to other Agents.
 * The text will be interpreted by a specific set of keys, where these keys each have their own set of keywords.
 * A keyword will be matched via the {@code isInterested(String)} method of an {@link InterestInterpreter}.
 * 
 * @author B-T-Johnson
 */
public class TextToIntention extends DevdasCore
{
	private ArrayList<InterestInterpreter> keyToInterests = new ArrayList<InterestInterpreter>();
	private TextToIntention self;
	
	public TextToIntention(Configuration config)
	{
		super(config);
		self = this; //Is this safe?
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
			System.err.println("Received AgentCommand " + cmd);
			
			if(cmd.getTopic().equals("Announcement") && cmd.getInterest().equals("Interests") && (cmd.getDestination().isEmpty() || cmd.getDestination() == null))
			{
				System.err.println("Command is identified as an InterestCommand");
				
				InterestInterpreter i = new InterestInterpreter(self, cmd.getSource(), cmd.getParamList("Interests"));
				System.err.println(addKeyToInterests(cmd.getSource(), i));
				
				announce();
				
				//Testing KeyToInterests methods
/*
				InterestInterpreter dummy = new InterestInterpreter("DUMMY", "BLANK1");
				System.err.println("=== TEST 1: MODIFY ===");
				System.err.println("TRUE = " + modifyKeyToInterests(cmd.getSource(), dummy));
				System.err.println("FALSE = " + modifyKeyToInterests("NOT AN AGENT", dummy));
				
				announce();
				
				InterestInterpreter dummy2 = new InterestInterpreter("NEW_DUMMY", "BLANK2", "BLANK3");
				System.err.println("=== TEST 2: ADD ===");
				System.err.println("TRUE = " + addKeyToInterests("NEW_DUMMY", dummy2));
				
				announce();
				
				System.err.println("TRUE = " + addKeyToInterests("NEW_DUMMY", dummy));
				System.err.println("FALSE = " + addKeyToInterests("NEW_DUMMY", dummy2));
				
				announce();
				
				System.err.println("=== TEST 3: REMOVE ===");
				System.err.println("TRUE = " + removeKeyToInterests("DUMMY"));
				System.err.println("FALSE = " + removeKeyToInterests("DUMMY"));
				
				announce();
 */
			}
		}
	}
	
	private class BestMatch implements AgentReaction
	{
		private ArrayList<AgentMessage> matchQueue = new ArrayList<AgentMessage>();
		
		public BestMatch()
		{}
		
		public void execute(AgentMessage command)
		{
			matchQueue.add(command);
			
			if(matchQueue.size() == keyToInterests.size())
			{
				AgentMessage max = matchQueue.get(0);
				
				for(int i = 1; i < matchQueue.size(); i++)
				{
					if(Double.valueOf(matchQueue.get(i).getParam("Match", 0)) > Double.valueOf(max.getParam("Match", 0)))
					{
						max = matchQueue.get(i);
					}
				}
				
				max.setTopic("Match"); //There's got to be a better name...
				
				sendAgentMessage(max.getInterest(), max);
				matchQueue.clear();
			}
		}
	}
	
	@SuppressWarnings("serial")
	public void initializeAgentReactions()
	{
		agentInterests.add("Announcement");
		agentInterests.add("ContextFreeText");
		agentInterests.add("InterestInterpreter");
		
		agentReactions.put("ContextFreeText", new ArrayList<AgentReaction>());
		agentReactions.put("Announcement", new ArrayList<AgentReaction>(){{add(new Initializer());}});
		agentReactions.put("InterestInterpreter", new ArrayList<AgentReaction>(){{add(new BestMatch());}});
	}
	
	public void agentActivity()
	{
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
	
	public void announce() //TODO May need to move up into Core as systemCommand method
	{
		System.out.println(agentReactions);
		System.out.println(keyToInterests);
	}
	
	/**
	 * Appends an InterestInterpreter to the end of the list of known interests for the specified agent. 
	 * 
	 * @param agent Desired agent key within the agent mapping
	 * @param keyToInterest InterestInterpreter that shall be added to the agent
	 * @return true if the mapping of interests and agents changed as a result of the call
	 */
	public boolean addKeyToInterests(String agent, InterestInterpreter keyToInterest)
	{
		boolean result = false;
		
		if(this.keyToInterests.contains(keyToInterest))
		{
			InterestInterpreter[] keys = this.getKeyToInterests();
			
			for(int i = 0; i < keys.length; i++)
			{	
				if(keys[i].getKeyToInterest().equals(agent))
				{
					result = ((InterestInterpreter) keyToInterests.get(i)).addKeyword(keyToInterest.getKeywords());
				}
			}
		}
		else
		{
			result = this.keyToInterests.add(keyToInterest);
			this.agentReactions.put("ContextFreeText", keyToInterests);
		}
		
		return result;
	}
	
	/**
	 * Removes the first InterestInterpreter associated to the specified agent from the agent mapping, if it is present. If the mapping does not contain the agent, it is unchanged.
	 * 
	 * @param agent Desired agent key within the agent mapping
	 * @return true if the mapping of interests and agents changed as a result of the call
	 */
	public boolean removeKeyToInterests(String agent)
	{
		for(InterestInterpreter i : this.getKeyToInterests())
		{
			if(i.getKeyToInterest().equals(agent))
			{
				return keyToInterests.remove(i);
			}
		}
		
		return false;
	}
	
	/**
	 * Replaces the first InterestInterpreter associated with the specified agent in the agent mapping with the specified element.
	 * 
	 * @param target Desired agent key within the agent mapping
	 * @param newKey InterestInterpreter to be stored in the mapping 
	 * @return true if the mapping of interests and agents changed as a result of the call
	 */
	public boolean modifyKeyToInterests(String target, InterestInterpreter newKey)
	{
		InterestInterpreter[] keys = this.getKeyToInterests();

		for(int i = 0; i < keys.length; i++)
		{
			if(keys[i].getKeyToInterest().equals(target))
			{
				return this.keyToInterests.set(i, newKey) != null;
			}
		}
			
		return false;
	}
	
	public InterestInterpreter[] getKeyToInterests()
	{
		return keyToInterests.toArray(new InterestInterpreter[keyToInterests.size()]);
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