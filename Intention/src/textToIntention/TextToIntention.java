package textToIntention;

import java.util.ArrayList;

import commandservice.AgentMessage;
import commandservice.AgentReaction;
import commandservice.CommandMessage;
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
	private static final double EPSILON = 10; //Is this *too* broad? Equivalent to a 10% difference
											  //Should this value be a constant or a function of text-length to negate the effect of long String-phrases?
	
	public TextToIntention(Configuration config)
	{
		super(config);
	}
	
	@Override
	public void announce(CommandMessage command)
	{
		super.announce(command);
		
		System.out.println(keyToInterests);
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
			
			if(cmd.getTopic().equals("Announcement") && cmd.getInterest().equals("Interests") && (cmd.getDestination().isEmpty()))
			{
				//System.err.println("Command is identified as an InterestCommand");
				
				InterestInterpreter i = new InterestInterpreter(TextToIntention.this, cmd.getSource(), cmd.getParamList("Interests"));
				System.err.println(addKeyToInterests(i));
				
				announce(null);
				
				//Testing KeyToInterests methods
/*
				InterestInterpreter dummy = new InterestInterpreter(TextToIntention.this, "DUMMY", "BLANK1");
				System.err.println("=== TEST 1: MODIFY ===");
				System.err.println("TRUE = " + modifyKeyToInterests(cmd.getSource(), dummy));
				System.err.println("FALSE = " + modifyKeyToInterests("NOT AN AGENT", dummy));
				
				announce(null);
				
				InterestInterpreter dummy2 = new InterestInterpreter(TextToIntention.this, "NEW_DUMMY", "BLANK2", "BLANK3");
				System.err.println("=== TEST 2: ADD ===");
				System.err.println("TRUE = " + addKeyToInterests(dummy2));
				
				announce(null);
				
				InterestInterpreter dummy3 = new InterestInterpreter(TextToIntention.this, "NEW_DUMMY", dummy.getKeywords());
				System.err.println("TRUE = " + addKeyToInterests(dummy3));
				System.err.println("FALSE = " + addKeyToInterests(dummy2));
				
				announce(null);
				
				System.err.println("=== TEST 3: REMOVE ===");
				System.err.println("TRUE = " + removeKeyToInterests("DUMMY"));
				System.err.println("TRUE = " + removeKeyToInterests(dummy3));
				System.err.println("FALSE = " + removeKeyToInterests(dummy));   //Already removed
				System.err.println("FALSE = " + removeKeyToInterests("DUMMY")); // " "
				
				announce(null);
 */				
			}
		}
	}
	
	/**
	 * Identifies which {@link InterestInterpreter} has the highest match percentage (according to the {@link InterestInterpreter#isInterested(String)} method in the {@link InterestInterpreter} class) and sends an {@link AgentMessage} to its associated Agent.
	 * If the match percentages between two or more InterestInterpreters are within {@value #EPSILON} percent of the highest percentage, this inner class will send messages to all the Agents 
	 * associated to each InterestInterpreter that are within this criteria.
	 */
	private class BestMatch implements AgentReaction
	{
		private ArrayList<AgentMessage> matchQueue = new ArrayList<AgentMessage>(); //Not a "queue" in the truest sense, but fulfills a similar role
		
		public BestMatch()
		{}
		
		public void execute(AgentMessage command)
		{
			matchQueue.add(command);
			
			//Only once all InterestInterpreters have sent a command can we determine the highest match percentage
			if(matchQueue.size() == keyToInterests.size())
			{
				AgentMessage highest = matchQueue.get(0);
				ArrayList<AgentMessage> closest = new ArrayList<>();
				
				//Determine which InterestInterpreter has the highest match percentage
				for(int i = 1; i < matchQueue.size(); i++)
				{
					if(Double.valueOf(matchQueue.get(i).getParam("Match", 0)) > Double.valueOf(highest.getParam("Match", 0)))
					{
						highest = matchQueue.get(i);
					}
				}
				
				if(Double.valueOf(highest.getParam("Match", 0)) != 0.0)
				{
					closest.add(highest);
					matchQueue.remove(highest);

					//Determine which (if any) InterestInterpreters are within proximity to the highest match percentage
					for(int i = 0; i < matchQueue.size(); i++)
					{
						if(Math.abs( Double.valueOf(matchQueue.get(i).getParam("Match", 0)) - Double.valueOf(highest.getParam("Match", 0)) ) <= EPSILON) //Approximately equal
						{
							closest.add(matchQueue.get(i));
						}
					}

					//Send a message to the Agents which are in the queue
					for(AgentMessage msg : closest)
					{
						msg.setTopic("Match"); //TODO There's got to be a better topic name...

						sendAgentMessage(msg.getInterest(), msg);
					}
				}
				
				//Empty the queue after sending the message(s)
				matchQueue.clear();
			}
		}
	}
	
	public void initializeAgentReactions()
	{
		agentInterests.add("Announcement");
		agentInterests.add("ContextFreeText");
		agentInterests.add("InterestInterpreter");
		
		agentReactions.put("ContextFreeText", new ArrayList<AgentReaction>());
		
			ArrayList<AgentReaction> init = new ArrayList<AgentReaction>();
			init.add(new Initializer());
		agentReactions.put("Announcement", init);
		
			ArrayList<AgentReaction> bm = new ArrayList<AgentReaction>();
			bm.add(new BestMatch());
		agentReactions.put("InterestInterpreter", bm);
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
	
	/**
	 * Appends an InterestInterpreter to the end of the list of known interests. 
	 * 
	 * @param keyToInterest InterestInterpreter that shall be added to the agent
	 * @return true if the mapping of interests and agents changed as a result of the call
	 */
	public boolean addKeyToInterests(InterestInterpreter keyToInterest)
	{
		Boolean result = null;
		
		if(keyToInterest == null)
			return false;
		
		InterestInterpreter[] keys = this.getKeyToInterests();
			
		for(int i = 0; i < keys.length; i++)
		{	
			if(keys[i].getKeyToInterest().equals(keyToInterest.getKeyToInterest()))
			{
				result = ((InterestInterpreter) keyToInterests.get(i)).addKeyword(keyToInterest.getKeywords());
			}
		}
		
		if(result == null)
		{
			result = keyToInterests.add(keyToInterest);
			this.agentReactions.put("ContextFreeText", keyToInterests);
		}
		
		return result;
	}
	
	/**
	 * Removes keywords from the first InterestInterpreter (or the whole {@code Object} if all of the keywords from the desired InterestInterpreter are found within {@code keyToInterest}) from the agent mapping, if it is present. If the mapping does not contain the InterestInterpreter, it is unchanged.
	 * 
	 * @param keyToInterest InterestInterpreter with an expected list of keywords to remove
	 * @return true if the mapping of interests and agents changed as a result of the call
	 */
	public boolean removeKeyToInterests(InterestInterpreter keyToInterest)
	{
		boolean result = false;
		InterestInterpreter[] keys = this.getKeyToInterests();
		
		for(int i = 0; i < keys.length; i++)
		{
			if(keys[i].getKeyToInterest().equals(keyToInterest.getKeyToInterest()))
			{
				result = keyToInterest.containsAll(keys[i]) ? keyToInterests.remove(i) != null : keyToInterests.get(i).removeKeyword(keyToInterest.getKeywords());
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
		boolean result = false;
		InterestInterpreter[] keys = this.getKeyToInterests();
		
		for(int i = 0; i < keys.length; i++)
		{
			if(keys[i].getKeyToInterest().equals(agent))
			{
				result = keyToInterests.remove(i) != null;
			}
		}
		
		return result;
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