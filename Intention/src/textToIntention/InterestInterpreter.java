package textToIntention;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

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
	private Set<String> keywords = new HashSet<>();
	private Set<String> matchedKeywords = new HashSet<>();
	private static DevdasCore host;
	private final static int PERCENTAGE_BASE = 10000; //Translates to 100.00 percent
	
	public InterestInterpreter(DevdasCore host, String keyToInterest, Collection<? extends String> keywords)
	{
		this.keyToInterest = keyToInterest;
		
		addKeyword(keywords);
		
		InterestInterpreter.host = host;
	}
	
	public InterestInterpreter(DevdasCore host, String keyToInterest, String... keywords)
	{
		this(host, keyToInterest, Arrays.asList(keywords));
	}
	
	/**
	 * Used to manipulate comparisons within the {@code isInterested()} method
	 */
	private class ComparisonObject implements Comparable<ComparisonObject>
	{
		private String str;
		
		public ComparisonObject(String str)
		{
			this.str = str.toLowerCase();
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof ComparisonObject)
			{
				String str = ((ComparisonObject) obj).str;
				return this.str.contains(str);
			}

			return false;
		}
		
		@Override
		public int compareTo(ComparisonObject obj)
		{
			if(this.str.contains(obj.str))
			{
				return 0;
			}
			
			return this.str.compareToIgnoreCase(obj.str);
		}
	}
	
	/**
	 * Checks to see if a String of raw text contains any matches to the set of keywords known by the Agent.
	 * 
	 * @param contextFreeText String phrase whose presence in the set of keywords is to be tested
	 * @return Returns the number of matches to the keywords in the String as a percentage (out of {@value #PERCENTAGE_BASE}})
	 */
	private int isInterested(String contextFreeText)
	{
		int matchCount = 0;
		
		if(contextFreeText != null && contextFreeText.length() != 0) //No sense in checking an empty reference
		{		
			TreeMap<ComparisonObject, String> kMap = new TreeMap<>();
			
			for(String key : keywords)
			{
				if(key == null)
				{
					System.err.println("NULL"); //ERROR; keys should never be null
				}
				else
				{
					ComparisonObject obj = new ComparisonObject(key);
					kMap.put(obj, key);
				}
			}
			
			for(String word: contextFreeText.split(" "))
			{
				ComparisonObject obj = new ComparisonObject(word);
				
				if(kMap.containsKey(obj))
				{
					matchCount++;
					matchedKeywords.add(word);
				}
			}
			
			return (matchCount * PERCENTAGE_BASE / keywords.size());
		}
		
		return 0;
	}
	
	/**
	 * Adds all of the keywords in the specified collection to the set of known keywords if they are not already present.
	 * 
	 * @param interest Collection containing keywords to be added to the set of known keywords
	 * @return true if the set of keywords changed as a result of the call
	 */
	public boolean addKeyword(Collection<? extends String> interest)
	{
		if (interest.contains("") || interest.contains(null))
			return false;
		
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
	public boolean removeKeyword(String... interest)
	{
		boolean result = false;
		
		for(String i : interest)
		{	
			if(keywords.size() > 1) //Should never remove all keywords (defeats the purpose of having an InterestInterpreter); must have at least one keyword
			{
				result = keywords.remove(i);
			}
			else //Has one or less keywords (less if the array was never set)
			{
				//Should we log an error here? Or should we just destroy the InterestInterpreter at this point (since it is an empty field at this point)?
			}
		}
		
		return result;
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
			if(target == "" || target == null)
				return false;
			
			return this.addKeyword(target);
		}
		else
		{
			if(newInterest == "" || newInterest == null)
				return false;
			
			return removeKeyword(target) && addKeyword(newInterest);
		}
	}
	
	public String[] getKeywords()
	{
		try
		{
			return keywords.toArray(new String[keywords.size()]);
		}
		catch(NullPointerException e)
		{
			return null;
		}
	}
	
	/**
	 * @return The value representing 100 percent (1) according to this class
	 */
	public static int getPercentageBase()
	{
		return PERCENTAGE_BASE;
	}
	
	@Override
	public String toString()
	{
		return "{" + keyToInterest + ":" + keywords.toString() + "}";
	}
	
	public String getKeyToInterest()
	{
		return keyToInterest;
	}
	
	/**
	 * Checks to see if this InterestInterpreter contains all of the elements in the specified collection.
	 * 
	 * @param keywords Collection of keywords to be checked for containment in this InterestInterpreter
	 * @return true if this InterestInterpreter contains all of the elements in the specified collection
	 */
	public boolean containsAll(Collection<? extends String> keywords)
	{
		return this.keywords.containsAll(keywords);
	}
	
	/**
	 * Checks to see if this InterestInterpreter contains all of the Strings in the specified array of keywords.
	 * 
	 * @param keywords Array of keywords to be checked for containment in this InterestInterpreter
	 * @return true if this InterestInterpreter contains all of the keywords in the specified array
	 */
	public boolean containsAll(String... keywords)
	{
		return containsAll(Arrays.asList(keywords));
	}
	
	public boolean containsAll(InterestInterpreter i)
	{
		return containsAll(i.keywords);
	}
	
	public void execute(AgentMessage cmd)
	{	
		//System.err.println("Received AgentCommand " + cmd);
		
		if(cmd.getTopic().equals("ContextFreeText"))
		{
			//System.err.println("Command is identified as a RawTextCommand");
			
			if(cmd.getSource().equals(this.keyToInterest) || (cmd.getInterest().isEmpty() || cmd.getInterest().equals("All"))) 
			{
				//System.err.println("RawTextCommand is being processed...");
				
				System.err.println("AGENT: " + this.keyToInterest);
				for(String phrase : cmd.getParamList("Text"))
				{
					System.err.println("\tTEXT: " + phrase);
					int interest = this.isInterested(phrase);
					System.err.println("\tMATCHES: " + interest);
					
					AgentMessage response = setResponse(cmd, interest);
					//System.err.println("Set CommandResponse " + response + " to Route " + response.getRoute());
					host.sendAgentMessage(host.getHostID(), response);
					matchedKeywords.clear();
				}	
			}
		}
	}
	
	/**
	 * Prepares an AgentMessage in response to the host TextToIntention Agent
	 * 
	 * @param cmd Original message
	 * @param count Total match percentage (from {@link #isInterested(String)} method)
	 * @return A new AgentMessage that is ready to be sent
	 */
	private AgentMessage setResponse(AgentMessage cmd, int count)
	{
		AgentMessage msg = new AgentMessage(cmd);
		msg.setTopic("InterestInterpreter");
		msg.setInterest(this.keyToInterest);
		msg.addParam("Match", Integer.toString(count));
		msg.addParam("Keyword", matchedKeywords.toArray(new String[matchedKeywords.size()]));
		
		return msg;
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
				System.out.printf("There is a %.2f%% match.%n", interpreter.isInterested(phraseResponse) / 100.0);
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