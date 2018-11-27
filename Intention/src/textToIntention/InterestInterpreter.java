package textToIntention;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import commandservice.AgentMessage;
import commandservice.AgentReaction;

/**
 * An {@code InterestInterpreter} searches an input of raw text (non-contextual) for any possible interest that matches the keywords assigned to the interest in question.
 * 
 * @author B-T-Johnson
 */
public class InterestInterpreter implements AgentReaction
{
	private String keyToInterest;
	private ArrayList<String> keywords = new ArrayList<>();
	
	public InterestInterpreter(String keyToInterest, String... keywords)
	{
		this.keyToInterest = keyToInterest;
		
		for(String key : keywords)
		{
			this.keywords.add(key);
		}
	}
	
	public InterestInterpreter(String keyToInterest, Collection<String> keywords)
	{
		this.keyToInterest = keyToInterest;
		
		for(String key : keywords)
		{
			this.keywords.add(key);
		}
	}
	
	/**
	 * Checks to see if a String of raw text contains any matches to the keywords known by the Agent
	 * 
	 * @param contextFreeText A String containing uninterpreted text
	 * @return Returns the number of matches to the keywords in the String
	 */
	public int isInterested(String contextFreeText)
	{
		int count = 0;
		
		if(contextFreeText != null)
			for(String word : contextFreeText.split(" "))
			{
				for(String key : keywords)
				{
					if(word.toLowerCase().contains(key.toLowerCase()))
					{
						count++;
					}
				}
			}
		
		return count;
	}
	
	public void addKeyword(String interest)
	{
		if(!keywords.contains(interest)) //Avoids duplicates
		{
			this.keywords.add(interest);
		}
		else
		{
			//Should we log an error here?
		}
	}
	
	public void removeKeyword(String interest)
	{
		if(keywords.size() > 1) //Should never remove all keywords; must have at least one keyword
		{
			keywords.remove(interest);
		}
		else //Has one or less keywords (less if the array was never set)
		{
			//Should we log an error here?
		}
	}
	
	public void modifyKeyword(String target, String newInterest)
	{
		if(!keywords.contains(target)) //Avoids modifying something that does not exist
		{
			this.addKeyword(target);
		}
		else
		{
			keywords.add(keywords.indexOf(target), newInterest);
			keywords.remove(target);
		}
	}
	
	public String getKeywords()
	{
		return keywords.toString();
	}
	
	@Override
	public String toString()
	{
		return "{" + keyToInterest + ":" + getKeywords() + "}";
	}
	
	public void execute(AgentMessage cmd)
	{
		System.err.println("Received RawTextCommand " + cmd);
		
		if(cmd.getTopic().equals("ContextFreeText"))
		{
			System.err.println("\tTEXT: " + cmd.getParam("Text").get(0));
			System.err.println("\tMATCHES: " + this.isInterested(cmd.getParam("Text").get(0))); //TODO Send a message to proper recipient
		}
	}
	
	/**
	 * Used for debugging and testing the class code
	 * 
	 * @param args - command line arguments
	 */
	// TODO replace with junit testing
	public static void main(String[] args)
	{
		Scanner keyboard = new Scanner(System.in);
		ArrayList<String> keywords = new ArrayList<String>();
		String keywordResponse;
		String phraseResponse;
		
		do
		{
			System.out.print("Construct a list of keywords [ENTER -1 TO STOP]: ");
				keywordResponse = keyboard.nextLine();
		
				if(!keywordResponse.equals("-1"))
				{
					keywords.add(keywordResponse);
				}
				else
				{
					break;
				}
		}
		while(true);
			
		System.out.println("========================================");
		
		InterestInterpreter interpreter = new InterestInterpreter("Test", keywords);
		
		System.out.println(interpreter);
		
		System.out.println("========================================");
		
		do
		{
			System.out.print("Enter a phrase to interpret [ENTER -1 TO QUIT]: ");
				phraseResponse = keyboard.nextLine();
			
			if(!phraseResponse.equals("-1"))
			{
				System.out.println(interpreter.isInterested(phraseResponse) == 1 ? "There is 1 match" : "There are " + interpreter.isInterested(phraseResponse) + " matches");
					System.out.println("----------------------------------------");
			}
			else
			{
				System.out.print("========================================");
				break;
			}
		}
		while(true);
		
		keyboard.close();
	}
}