package textToIntention;

import java.util.ArrayList;
import java.util.Scanner;
import commandservice.AgentMessage;
import commandservice.AgentReaction;

/**
 * An {@code InterestInterpreter} searches an input of raw text (non-contextual) for any possible interest that matches the keywords assigned to the interest in question
 * 
 * @author B-T-Johnson
 */
public class InterestInterpreter implements AgentReaction
{
	private String keyToInterest;
	private ArrayList<String> keywords;
	
	public InterestInterpreter()
	{
		this.keyToInterest = new String();
		this.keywords = new ArrayList<String>();
	}
	
	public InterestInterpreter(String keyToInterest)
	{
		this.keyToInterest = keyToInterest;
		this.keywords = new ArrayList<String>();
	}
	
	public InterestInterpreter(String interest, ArrayList<String> keywords)
	{
		this.keyToInterest = interest;
		this.keywords = keywords;
	}
	
	public InterestInterpreter(String interest, String keyword)
	{
		this.keyToInterest = interest;
		this.keywords.add(keyword);
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
		if(!keyToInterest.contains(target)) //Avoids modifying something that does not exist
		{
			this.addKeyword(target);
		}
		else
		{
			keywords.add(keywords.indexOf(target), newInterest);
			keywords.remove(target);
		}
	}
	
	public String getKeyToInterest()
	{
		return keyToInterest;
	}
	
	public void setKeyToInterest(String key)
	{
		this.keyToInterest = key;
	}
	
	public Object[] getKeywords()
	{
		return keywords.toArray();
	}
	
	@Override
	public String toString()
	{
		String result = "\"" + getKeyToInterest() + "\"" + ":" + "{";
		
		for(int i = 0; i < getKeywords().length; i++)
		{
			if(i == (getKeywords().length - 1))
			{
				result += "\"" + keywords.toArray()[i] + "\"" + "}";
			}
			else
			{
				result += "\"" + keywords.toArray()[i] + "\"" + ",";
			}
		}
		
		return result;
	}
	
	public void execute(AgentMessage cmd)
	{
		System.out.println(this.isInterested(cmd.getParam("ContextFreeText")));
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
				System.out.println("========================================");
				break;
			}
		}
		while(true);
		
		keyboard.close();
	}
}