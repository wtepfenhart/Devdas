package textToIntention;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import commandservice.AgentMessage;
import commandservice.AgentReaction;
import commandservice.DevdasCore;
import devdas.Configuration;

/**
 * An {@code InterestInterpreter} searches an input of raw text (non-contextual) for any possible interest that matches the keywords assigned to the interest in question.
 * 
 * @author B-T-Johnson
 */
public class InterestInterpreter implements AgentReaction
{
	private String keyToInterest;
	private AgentMessage response;
	private Set<String> keywords = new HashSet<>();
	private DevdasCore host;
	
	public InterestInterpreter(DevdasCore host, String keyToInterest, Collection<? extends String> keywords)
	{
		this.keyToInterest = keyToInterest;
		
		this.keywords.addAll(keywords);
		
		this.host = host;
	}
	
	public InterestInterpreter(DevdasCore host, String keyToInterest, String... keywords)
	{
		this(host, keyToInterest, Arrays.asList(keywords));
	}
	
	/**
	 * Checks to see if a String of raw text contains any matches to the set of keywords known by the Agent.
	 * 
	 * @param contextFreeText String phrase whose presence in the set of keywords is to be tested
	 * @return Returns the number of matches to the keywords in the String as a percentage (out of 100)
	 */
	public double isInterested(String contextFreeText)
	{
		int wordCount = 0;
		int searchCount = 0;
		int matchCount = 0;
		
		if(contextFreeText != null)
			for(String word : contextFreeText.split(" "))
			{
				wordCount++;
				
				for(String key : keywords)
				{
					searchCount++;
					
					if(key == null)
					{
						System.err.println("NULL"); //ERROR; keys should never be null
					}
					else if(word.toLowerCase().contains(key.toLowerCase()))
					{
						matchCount++;
					}
				}
			}
		
		return (((double) matchCount / searchCount) / wordCount) * 100;
	}
	
	/**
	 * Adds all of the keywords in the specified collection to the set of known keywords if they are not already present.
	 * 
	 * @param interest Collection containing keywords to be added to the set of known keywords
	 * @return true if the set of keywords changed as a result of the call
	 */
	public boolean addKeyword(Collection<? extends String> interest)
	{
		return this.keywords.addAll(interest);
	}
	
	/**
	 * Adds all of the keywords in the array to the set of known keywords if they are not already present.
	 * 
	 * @param interest Array containing keywords to be added to the set of known keywords
	 * @return true if the set of keywords changed as a result of the call
	 */
	public boolean addKeyword(String... interest)
	{
		return this.addKeyword(Arrays.asList(interest));
	}
	
	/**
	 * Removes the specified keyword from the set of known keywords if it is present.
	 * 
	 * @param interest Keyword to be removed from the set of keywords, if present
	 * @return true if the set of keywords contained the specified element
	 */
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
	
	/**
	 * Replaces the specified keyword within the set of known keywords with a new keyword, if present. If the specified keyword is not within the set,
	 * the specified keyword is added to the set instead.
	 * 
	 * @param target element whose presence in the set of keywords is to be tested
	 * @param newInterest Keywords to be added to the set of known keywords
	 * @return true if the set of keywords changed as a result of the call
	 */
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
	
	public String[] getKeywords()
	{
		return keywords.toArray(new String[keywords.size()]);
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
			return this.keyToInterest.equals(i.keyToInterest) && this.keywords.containsAll(i.keywords);
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
		System.err.println("Received AgentCommand " + cmd);
		
		if(cmd.getTopic().equals("ContextFreeText"))
		{
			System.err.println("Command is identified as a RawTextCommand");
			
			//TODO We need a standardized format for commands that do not specify a field in advance; either null or a blank String, not both
			if(cmd.getSource().equals(this.keyToInterest) || (cmd.getInterest() == null || cmd.getInterest().equals("All"))) 
			{
				System.err.println("RawTextCommand is being processed...");

				double count = 0;
				
				for(String phrase : cmd.getParamList("Text"))
				{
					System.err.println("\tTEXT: " + phrase);
					count += this.isInterested(phrase);
					System.err.println("\tMATCHES: " + this.isInterested(phrase));
				}
				
				//TODO Send a message to proper recipient
				setResponse(cmd, count);
				System.err.println("Set CommandResponse " + this.response + " to Route " + this.response.getRoute());
				host.sendAgentMessage(this.host.getHostID(), this.response);
			}
		}
	}
	
	public void setResponse(AgentMessage cmd, double count)
	{
		AgentMessage msg = new AgentMessage(cmd);
		msg.setTopic("InterestInterpreter");
		msg.setInterest(this.keyToInterest);
		msg.addParam("Match", Double.toString(count));
		
		this.response = msg;
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
		
		InterestInterpreter interpreter = new InterestInterpreter(new TextToIntention(new Configuration(args)),"Test", keywords);
		
		System.out.println(interpreter);
		
		System.out.println("========================================");
		
		do
		{
			System.out.print("Enter a phrase to interpret [ENTER -1 TO QUIT]: ");
				phraseResponse = keyboard.nextLine();
			
			if(!phraseResponse.equals("-1"))
			{
				System.out.printf("There is a %.2f%% match.%n", interpreter.isInterested(phraseResponse));
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