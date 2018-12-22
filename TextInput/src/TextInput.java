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
		
			
		
			System.out.println("Enter input:");
			String msg = scanner.nextLine();
			AgentMessage a = new AgentMessage();
			System.out.println("Do you want to send the message to any specfic place?");
			String answer = scanner.nextLine();
			if(answer.equals("Yes"))
			{
				System.out.println("Where do you want to send it?");
				String d = scanner.nextLine();
				a.setDestination(d);
				//Agent Message is sent to designated agent
				a.setTopic("ContextReliantText");
				a.addParam("Subject", msg);
				sendAgentMessage(a.getRoute(),a);
				//Agent Message is sent to text output (default agent)
				AgentMessage b = new AgentMessage();
				b.setTopic("ContextReliantText");
				b.addParam("Subject", msg);
				sendAgentMessage(b.getRoute(),b);
				processAgentMessage(a);
				processAgentMessage(b);
			}
				
			else
			{
				a.setTopic("ContextFreeText");
				a.addParam("Subject", msg);
				sendAgentMessage(a.getRoute(),a);
				processAgentMessage(a);
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






