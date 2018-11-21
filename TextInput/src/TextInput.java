/**
 * @author wtepfenhart
 *@author Nicholas-Jason Roache
 *
 */

//import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import commandservice.DevdasCore;
import devdas.Configuration;
import commandservice.AgentReaction;
import commandservice.AgentMessage;
public class TextInput extends DevdasCore  {

	
	Scanner scanner;
	private static Configuration config;
	//constants
	private final static String QUEUE_NAME = "Request";
	
	/**
	 * Has random variables to make testing easier
	 */
	public TextInput()
	{
		super(config);
	}
	public TextInput( Configuration config) {
		// TODO Auto-generated constructor stub
		super(config);
		scanner = new Scanner(System.in);
		
	}

	
	/**
	 * the following is for testing junit testing (can you say recursion?)
	 */
	public void printHello() {
		System.out.println("Hello Cruel World");
	}

	
	
	
	/**
	 * @param args
	 */
	//abstract methods
	public void agentActivity()
	{
		
			//Placeholder for Joker agent
			TextInput j = new TextInput();
		
			System.out.println("Enter input:");
			String msg = scanner.nextLine();
			AgentMessage a = new AgentMessage();
			
			if(a.getDestination() != "")
			{
				//Agent Message is sent to designated agent
				a.addParam("Subject", msg);
				a.setTopic("ContextReliantText");
				sendAgentMessage(a.getRoute(),a);
				//Agent Message is sent to text output (default agent)
				AgentMessage b = new AgentMessage();
				b.addParam("Subject", msg);
				b.setTopic("ContextReliantText");
				sendAgentMessage(b.getRoute(),b);
			}
			else
			{
				a.addParam("Subject", msg);
				a.setTopic("ContextFreeText");
				sendAgentMessage(a.getRoute(),a);
			}
		}
		
		
	//}
	
	@Override
	public void initializeAgentReactions() {
		// TODO Auto-generated method stub

	}


	public static void main(String[] args)  {
		// TODO Auto-generated method stub
		Configuration c = new Configuration(args);
		TextInput t = new TextInput(c);
		
		try {
			System.out.println(t.getClass().getSimpleName() + " " + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.run();
		
	}

	
}

