package textToIntention;

import java.util.ArrayList;

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
	@SuppressWarnings("unused")
	private ArrayList<String> keyToInterests;
	
	public TextToIntention(Configuration config, String...keyToInterests)
	{
		super(config);
		
		initializeInterpreters(keyToInterests);
	}
	
	public void initializeAgentReactions()
	{
		agentInterests.add("ContextFreeText");
	}
	
	public void initializeInterpreters(String...keyToInterests)
	{
		//Not sure how to "un-nest" this; was in initializeAgentReactions(), but would cause a NullPuinterException (since keyToInterests is only instantiated after the method is called)
		for(String key : keyToInterests)
		{
			this.keyToInterests.add(key);
			agentReactions.put("ContextFreeText", new InterestInterpreter(key));
		}
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
	
	public void announce()
	{
		System.out.println(agentReactions.values());
	}
	
	public void addKeyToInterests(String...keyToInterests)
	{
		for(String key : keyToInterests)
		{
			if (!this.keyToInterests.contains(key))
			{
				this.keyToInterests.add(key);
				agentReactions.put("ContextFreeText", new InterestInterpreter(key));
			}
			else
			{
				//Should we log an error?
			}
		}
	}
	
	public void removeKeyToInterests(String...keyToInterests)
	{
		for(String key : keyToInterests)
		{
			if (!this.keyToInterests.contains(key))
			{
				if(this.keyToInterests.size() > 1)
				{
					this.keyToInterests.remove(key);
					agentReactions.remove("ContextFreeText", key);
				}
			}
			else
			{
				//Should we log an error?
			}
		}
	}
	
	public void modifyKeyToInterests(String target, String newKey)
	{
		agentReactions.replace("ContextFreeText", agentReactions.get(target), new InterestInterpreter(newKey));
	}

	/**
	 * Used for debugging and testing the class code
	 * 
	 * @param args - command line arguments
	 */
	// TODO replace with junit testing
	public static void main(String[] args) //ERROR; something wrong with build-path (cannot find user.ini)
	{
		Configuration config = new Configuration(args);
		TextToIntention tester = new TextToIntention(config, "Tester");
		tester.run();
	}
}