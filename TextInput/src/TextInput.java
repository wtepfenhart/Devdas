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
			AgentMessage a;
			if(msg.contains("?") == true)
			{
				//Message a is to be sent to the TextOupt Agent
				a = new AgentMessage();
				a.addParam("Subject", msg);
				a.setTopic("ContextReliantText");
				TextOutput o;
				String d1 = o.hostID;
				a.setDestination(d1);
				sendAgentMessage(a.getRoute(),a);
				//Message b is to sent to the Jokes Agent
				AgentMessage b = new AgentMessage();
				b.addParam("Subject", msg);
				b.setTopic("ContextReliantText");
				//Placeholder for jokes agent
				String d2 = j.hostID;
				b.setDestination(d2);
				sendAgentMessage(b.getRoute(),b);
				processAgentMessage(b);
				processAgentMessage(a);
				
			}
			else
			{
				a = new AgentMessage();
				a.addParam("Subject",msg);
				a.setTopic("Statement");
				sendAgentMessage(a.getRoute(),a);
				processAgentMessage(a);
				
			}
			if(j.isRunning()== true)
			{
				try
				{
					Thread.sleep(5000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
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


