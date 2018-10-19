package textToIntention;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class acts to search an input of raw text (non-contextual) for any possible interest that matches a specific key associated with an agent
 * 
 * @author B-T-Johnson
 */
public class InterestInterpretor
{
	private String interest; //What *is* this for?
	private ArrayList<String> keywords;
	
	public InterestInterpretor(ArrayList<String> keywords)
	{
		this.keywords = keywords;
	}
	
	/**
	 * Checks to see if a String of raw text contains any matches to the keywords known by the agent
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
	
	public void addInterest(String interest)
	{
		if(!keywords.contains(interest))
		{
			keywords.add(interest);
		}
	}
	
	public void removeInterest(String interest)
	{
		keywords.remove(interest);
	}
	
	public void modifyInterest(String target, String newInterest)
	{
		if(!keywords.contains(target))
		{
			this.addInterest(target);
		}
		else
		{
			keywords.add(keywords.indexOf(target), newInterest);
			keywords.remove(target);
		}
	}
	
	public ArrayList<String> getInterests()
	{
		return keywords;
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
					System.out.println("\t" + keywords);
					break;
				}
		}
		while(true);
			
		System.out.println("========================================");
		
		InterestInterpretor interpretor = new InterestInterpretor(keywords);
		
		do
		{
			System.out.print("Enter a phrase to interpret [ENTER -1 TO QUIT]: ");
				phraseResponse = keyboard.nextLine();
			
			if(!phraseResponse.equals("-1"))
			{
				System.out.println(interpretor.isInterested(phraseResponse) == 1 ? "There is 1 match" : "There are " + interpretor.isInterested(phraseResponse) + " matches");
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