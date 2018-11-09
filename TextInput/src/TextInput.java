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
		
		DevdasCore j = new DevdasCore();
		
		if(j.isRunning() == true)
		{
			//Substitute for actually code
			try
			{
				Thread.sleep(50);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			
		}
		else
		
		
			System.out.println("Enter input:");
			String msg = scanner.nextLine();
			AgentMessage a;
			if(msg.contains("?") == true)
			{
				a = new AgentMessage();
				AgentMessage b = new AgentMessage();
				a.addParam("Subject", msg);
				a.setTopic("ContextReliantText");
				sendAgentMessage(a.getRoute(),a);
				b.addParam("Subject", msg);
				b.setTopic("ContextReliantText");
				//Placeholder for jokes agent
				b.setDestination("Jokes Agent");
				sendAgentMessage(b.getRoute(),b);
				
			}
			else
			{
				a = new AgentMessage();
				a.addParam("Subject",msg);
				a.setTopic("Statement");
				sendAgentMessage(a.getRoute(),a);
				processAgentMessage(a);
				
			}
		}
		
		
	//}
	
	@Override
	public void initializeAgentReactions() {
		// TODO Auto-generated method stub
		agentInterests.add("Statement");
		agentReactions.put("Statement", new Statement());
		agentInterests.add("ContextReliantText");
		agentReactions.put("ContextReliantText", new ContextReliantText());

	}
	
	 public class Statement implements AgentReaction
	 {
		public Statement()
		{
			
		}
		public void execute(AgentMessage cmd)
		{
			System.out.println(cmd.getParam("Subject"));
		}
	 }
	
	 public class ContextReliantText implements AgentReaction
	 {
		 public ContextReliantText()
		 {
			 
		 }
		 public void execute(AgentMessage cmd)
		 {
			 System.out.println(cmd.getParam("Subject"));
	
			 
		 }
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

