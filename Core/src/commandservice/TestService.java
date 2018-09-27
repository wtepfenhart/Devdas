/**
 * 
 * Author: wtepfenhart
 * File: TestService.java
 * Date: Sep 26, 2018
 * Copyright (C) 2018
 *
 */
package commandservice;

import java.util.ArrayList;
import java.util.HashMap;

import devdas.Configuration;

/**
 * @author wtepfenhart
 *
 */
public class TestService implements AgentProcessor {
	DevdasCoreTester app;

	public TestService(DevdasCoreTester anApp) {
		app = anApp;
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see commandservice.AgentProcessor#execute(commandservice.AgentMessage)
	 */
	@Override
	public void execute(AgentMessage command) {
		// TODO Auto-generated method stub
		app.test(command);
		return;
	}

}
