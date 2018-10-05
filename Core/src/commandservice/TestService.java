/**
 * 
 * Author: wtepfenhart
 * File: TestService.java
 * Date: Sep 26, 2018
 * Copyright (C) 2018
 *
 */
package commandservice;

/**
 * @author wtepfenhart
 *
 */
public class TestService implements AgentReaction {
	DevdasCoreTester app;

	public TestService(DevdasCoreTester anApp) {
		app = anApp;
	}

	/* (non-Javadoc)
	 * @see commandservice.AgentReaction#execute(commandservice.AgentMessage)
	 */
	@Override
	public void execute(AgentMessage command) {
		app.test(command);
		return;
	}

}
