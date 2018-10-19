package textToIntention;

import commandservice.AgentMessage;
import commandservice.AgentReaction;
import commandservice.DevdasCore;
import devdas.Configuration;

/**
 * @author B-T-Johnson
 */
public class TextToIntention extends DevdasCore
{	
	private InterestInterpretor interpretor;
	
	public TextToIntention(Configuration config)
	{
		super(config);
		this.interpretor = new InterestInterpretor(agentInterests);
	}
	
	public class Interpret implements AgentReaction
	{	
		/**
		 * Default Constructor
		 */
		public Interpret()
		{}
		
		public void execute(AgentMessage cmd)
		{
			System.out.println(interpretor.isInterested(cmd.getParam("ContextFreeText")));
		}
	}
	
	public void initializeAgentReactions()
	{
		agentInterests.add("Intention");
		agentReactions.put("Intention", new Interpret());
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
	
	public String announce()
	{
		return interpretor.getInterests().toString();
	}

	/**
	 * Used for debugging and testing the class code
	 * 
	 * @param args - command line arguments
	 */
	// TODO replace with junit testing
	public static void main(String[] args)
	{
		 
	}
}