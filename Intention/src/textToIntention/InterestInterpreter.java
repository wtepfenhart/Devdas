package textToIntention;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

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
	private Set<String> keywords = new HashSet<>();
	
	public InterestInterpreter(String keyToInterest, String... keywords)
	{
		this.keyToInterest = keyToInterest;
		
		for(String key : keywords)
		{
			this.keywords.add(key);
		}
	}
	
	public InterestInterpreter(String keyToInterest, Collection<? extends String> keywords)
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
					if(key == null)
					{
						System.err.println("NULL");//ERROR; keys should never be null
					}
					else if(word.toLowerCase().contains(key.toLowerCase()))
					{
						count++;
					}
				}
			}
		
		return count;
	}
	
	public boolean addKeyword(Collection<? extends String> interest)
	{
		return this.keywords.addAll(interest);
	}
	
	public boolean addKeyword(String... interest)
	{
		return this.addKeyword(Arrays.asList(interest));
	}
	
	public boolean removeKeyword(String interest)
	{
		if(keywords.size() > 1) //Should never remove all keywords; must have at least one keyword
		{
			return keywords.remove(interest);
		}
		else //Has one or less keywords (less if the array was never set)
		{
			return false;
			//Should we log an error here?
		}
	}
	
	public boolean modifyKeyword(String target, String newInterest)
	{
		if(!keywords.contains(target)) //Avoids modifying something that does not exist
		{
			return this.addKeyword(target);
		}
		else
		{
			return keywords.remove(target) && keywords.add(newInterest);
		}
	}
	
	public Collection<? extends String> getKeywords()
	{
		return keywords;
	}
	
	@Override
	public String toString()
	{
		return "{" + keyToInterest + ":" + keywords.toString() + "}";
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof InterestInterpreter)
		{
			InterestInterpreter i = (InterestInterpreter) o;
			return i.keyToInterest.equals(this.keyToInterest) && this.keywords.containsAll(i.keywords);
		}
		else
			return false;
	}
	
	public String getKeyToInterest()
	{
		return keyToInterest;
	}
	
	public void execute(AgentMessage cmd)
	{
		System.err.println("Received Command " + cmd);
		
		if(cmd.getTopic().equals("ContextFreeText"))
		{
			System.err.println("Command is identified as a RawTextCommand");
			
			//TODO We need a standardized format for commands that do not specify a destination; either null or a blank String
			if(cmd.getInterest().equals(this.keyToInterest) || (cmd.getInterest().equals(null) || cmd.getInterest().equals("") || cmd.getInterest().equals("All")) ) 
			{
				System.err.println("RawTextCommand is being processed...");

				System.err.println("\tTEXT: " + cmd.getParam("Text")[0]);
				System.err.println("\tMATCHES: " + this.isInterested(cmd.getParam("Text")[0]));
				
				//TODO Send a message to proper recipient
			}
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