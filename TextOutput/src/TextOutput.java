import commandservice.DevdasCore;
import devdas.Configuration;
import commandservice.AgentReaction;
import commandservice.AgentMessage;

/**
 * 
 * Author: wtepfenhart
 * File: TextOutput.java
 * Date: Sep 24, 2018
 * Copyright (C) 2018
 *
 */

/**
 * @author wtepfenhart
 * @author Nicholas-Jason Roache
 */

public class TextOutput extends DevdasCore{

	/**
	 * 
	 */
	private static Configuration config;
	public TextOutput()  {
		// TODO Auto-generated constructor stub
		super(config);
	}

	/**
	 * @param args
	 */

	public class Response implements AgentReaction
	{
		public Response()
		{
			
		}

		@Override
		public void execute(AgentMessage command) {
			// TODO Auto-generated method stub
			System.out.println(command.getParam("Response"));
		}
		
	}
	
	@Override
	public void initializeAgentReactions() {
		// TODO Auto-generated method stub
		agentInterests.add("Response");
		agentReactions.put("Response", new Response());
	}

	

	@Override
	public void agentActivity() {
		// TODO Auto-generated method stub
		try
		{
			Thread.sleep(10);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		
	}
	public static void main(String [] args)
	{
		TextOutput tester = new TextOutput();
		tester.run();
	}
}
