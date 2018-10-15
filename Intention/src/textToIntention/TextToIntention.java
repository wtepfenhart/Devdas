package textToIntention;

import java.util.ArrayList;
import commandservice.AgentMessage;
import commandservice.AgentReaction;
import commandservice.DevdasCore;
import devdas.Configuration;

/**
 * @author B-T-Johnson
 */
public class TextToIntention extends DevdasCore
{
	private ArrayList<String> intent;
	
	public TextToIntention(Configuration config)
	{
		super(config);
		
		intent = new ArrayList<String>();
	}
	
	public void addIntent(String keyword)
	{
		intent.add(keyword);
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
			System.out.println(isInterested(cmd));
		}
	}
	
	public boolean isInterested(AgentMessage msg)
	{	
		for(String keyword : intent)
		{
			return msg.getParam("Intent").contains(keyword);
		}
		
		return false;
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
//			if()
//			{
//				
//			}
//			else
			{
				Thread.sleep(10);
			}
		}
		catch(InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{

	}
}