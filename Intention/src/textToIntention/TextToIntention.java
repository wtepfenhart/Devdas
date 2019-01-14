package textToIntention;

import java.util.ArrayList;

import commandservice.AgentMessage;
import commandservice.AgentReaction;
import commandservice.DevdasCore;
import devdas.Configuration; 

/**
 * A {@code TextToIntention} Agent serves to interpret raw, context-free text into commands to be sent out to other Agents.
 * The text will be interpreted by a specific set of keys, where these keys each have their own set of keywords.
 * A keyword will be matched via the {@link InterestInterpreter#isInterested(String)} method of an {@link InterestInterpreter}.
 * 
 * @author B-T-Johnson
 */
public class TextToIntention extends DevdasCore
{
	private ArrayList<InterestInterpreter> keyToInterests = new ArrayList<InterestInterpreter>();
	private static final double EPSILON = 1; //Is this *too* precise? Equivalent to a 1% difference
	
	public TextToIntention(Configuration config)
	{
		super(config);
	}
	
	/**
	 * Helps construct all {@link InterestInterpreter}s needed by a particular Agent
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
			//System.err.println("Received AgentCommand " + cmd);
			
			if(cmd.getTopic().equals("Announcement") && cmd.getInterest().equals("Interests") && (cmd.getDestination().isEmpty() || cmd.getDestination() == null))
			{
				//System.err.println("Command is identified as an InterestCommand");
				
				InterestInterpreter i = new InterestInterpreter(TextToIntention.this, cmd.getSource(), cmd.getParamList("Interests"));
				System.err.println(addKeyToInterests(cmd.getSource(), i));
				
				announce();
				
				//Testing KeyToInterests methods
/*
				InterestInterpreter dummy = new InterestInterpreter(TextToIntention.this, "DUMMY", "BLANK1");
				System.err.println("=== TEST 1: MODIFY ===");
				System.err.println("TRUE = " + modifyKeyToInterests(cmd.getSource(), dummy));
				System.err.println("FALSE = " + modifyKeyToInterests("NOT AN AGENT", dummy));
				
				announce();
				
				InterestInterpreter dummy2 = new InterestInterpreter(TextToIntention.this, "NEW_DUMMY", "BLANK2", "BLANK3");
				System.err.println("=== TEST 2: ADD ===");
				System.err.println("TRUE = " + addKeyToInterests("NEW_DUMMY", dummy2));
				
				announce();
				
				System.err.println("TRUE = " + addKeyToInterests("NEW_DUMMY", dummy));
				System.err.println("FALSE = " + addKeyToInterests("NEW_DUMMY", dummy2));
				
				announce();
				
				System.err.println("=== TEST 3: REMOVE ===");
				System.err.println("TRUE = " + removeKeyToInterests("DUMMY"));
				System.err.println("TRUE = " + removeKeyToInterests("NEW_DUMMY", dummy));
				System.err.println("FALSE = " + removeKeyToInterests("NEW_DUMMY", dummy)); //Already removed
				System.err.println("FALSE = " + removeKeyToInterests("DUMMY"));			   // " "
				
				announce();
 */ 
			}
		}
	}
	
	/**
	 * Identifies which {@link InterestInterpreter} has the highest match percentage (according to the {@link InterestInterpreter#isInterested(String)} method in the {@link InterestInterpreter} class) and sends an {@link AgentMessage} to its associated Agent.
	 * If the match percentages between two or more InterestInterpreters are within {@value #EPSILON} of the highest percentage, this inner class will send messages to all the Agents 
	 * associated to each InterestInterpreter that are within this criteria.
	 */
	private class BestMatch implements AgentReaction
	{
		private ArrayList<AgentMessage> matchQueue = new ArrayList<AgentMessage>();
		
		public BestMatch()
		{}
		
		public void execute(AgentMessage command)
		{
			matchQueue.add(command);
			
			//Only once all InterestInterpreters have sent a command can we determine the highest match percentage
			if(matchQueue.size() == keyToInterests.size())
			{
				ArrayList<AgentMessage> max = new ArrayList<>();
				max.add(matchQueue.get(0));
				
				//Determine which InterestInterpreter has the highest match percentage (within a certain proximity)
				for(int i = 1; i < matchQueue.size(); i++)
				{
					if(Double.valueOf(matchQueue.get(i).getParam("Match", 0)) > Double.valueOf(max.get(0).getParam("Match", 0)))
					{
						max.clear();
						max.add(matchQueue.get(i));
					}
					else if(Double.valueOf(matchQueue.get(i).getParam("Match", 0)) + EPSILON > Double.valueOf(max.get(0).getParam("Match", 0))) //Approximately equal
					{
						max.add(matchQueue.get(i));
					}
				}
				
				//Send a message to the Agents which are in the queue
				for(AgentMessage msg : max)
				{
					msg.setTopic("Match"); //TODO There's got to be a better topic name...
					
					sendAgentMessage(msg.getInterest(), msg);
				}
				
				//Empty the queue after sending the message(s)
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
		
		if(keyToInterests.contains(keyToInterest))
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
			result = keyToInterests.add(keyToInterest);
			this.agentReactions.put("ContextFreeText", keyToInterests);
		}
		
		return result;
	}
	
	/**
	 * Removes keywords from the first InterestInterpreter (or the whole {@code Object} if all of the keywords from the desired InterestInterpreter are found within {@code keyToInterest} or if {@code keyToInterest} is {@code null}) that is associated to the specified agent from the agent mapping, if it is present. If the mapping does not contain the agent, it is unchanged.
	 * 
	 * @param agent Desired agent key within the agent mapping
	 * @param keyToInterest InterestInterpreter with an expected list of keywords to remove
	 * @return true if the mapping of interests and agents changed as a result of the call
	 */
	public boolean removeKeyToInterests(String agent, InterestInterpreter keyToInterest)
	{
		boolean result = false;
		InterestInterpreter[] keys = this.getKeyToInterests();
		
		for(int i = 0; i< keys.length; i++)
		{
			if(keys[i].getKeyToInterest().equals(agent))
			{
				result = keyToInterest == null || keyToInterest.equals(keyToInterests.get(i)) ? keyToInterests.remove(i) != null : keyToInterests.get(i).removeKeyword(keyToInterest.getKeywords());
			}
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
		return removeKeyToInterests(agent, null);
	}
	
	/**
	 * Replaces the first InterestInterpreter associated with the specified agent in the agent mapping with the specified element.
	 * 
	 * @param target Desired agent key within the agent mapping
	 * @param newKey InterestInterpreter to be stored in place of the original element
	 * @return true if the mapping of interests and agents changed as a result of the call
	 */
	public boolean modifyKeyToInterests(String target, InterestInterpreter newKey)
	{
		InterestInterpreter[] keys = this.getKeyToInterests();

		for(int i = 0; i < keys.length; i++)
		{
			if(keys[i].getKeyToInterest().equals(target))
			{
				return keyToInterests.set(i, newKey) != null;
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