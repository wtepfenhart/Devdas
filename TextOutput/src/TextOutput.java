import commandservice.DevdasCore;
import devdas.Configuration;

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
 *
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


	@Override
	public void initializeAgentReactions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeAgentInterests() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void agentActivity() {
		// TODO Auto-generated method stub
		
		
	}
	public static void main(String [] args)
	{
		TextOutput tester = new TextOutput();
		
	}
}
