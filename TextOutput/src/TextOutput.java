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
	
	public TextOutput(Configuration config)  {
		// TODO Auto-generated constructor stub
		super(config);
	}

	/**
	 * @param args
	 */

	public class Say implements AgentReaction
	{
		public Say()
		{
			
		}

		@Override
		public void execute(AgentMessage command) {
			// TODO Auto-generated method stub
			System.out.println(command.getParam("Subject"));
		}
		
	}
	
	@Override
	public void initializeAgentReactions() {
		// TODO Auto-generated method stub
		agentInterests.add("ContextFreeText");
		agentReactions.put("ContextFreeText", new Say());
		agentInterests.add("ContextReliantText");
		agentReactions.put("ContextReliantText", new Say());
	}

	

	@Override
	public void agentActivity() {
		// Placeholder Text
		AgentMessage m = new AgentMessage();
		String topic = m.getTopic("Topic");
		getAgentReaction(topic);
		
	}
	public static void main(String [] args)
	{
		Configuration c = new Configuration(args);
		TextOutput tester = new TextOutput(c);
		tester.run();
	}
}

