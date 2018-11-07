/**	
 * @author Hamza Zafar
 * This class receives input from the user, checks the nature of the command and broadcasts an AgentMessage with
 * an appropriate Topic.  
 */

package text.input;
import java.util.Scanner;

import commandservice.AgentMessage;
import commandservice.DevdasCore;
import devdas.Configuration;
import globals.Globals;
import globals.Topics;
import globals.Triggers;
import greeting.Greeting;

public class TextInput extends DevdasCore{

	private Scanner scanner;
	private static TextInput instance; 
	
	private TextInput(Configuration config) {
		super(config);
		this.scanner = new Scanner(System.in);
	}
	
	public static synchronized void init(){
		 if(instance == null){
			 System.out.println("Made text input agent instance.");
			 Configuration config = new Configuration(Globals.args);
	            instance = new TextInput(config);
		 }
	}

	public static synchronized TextInput getInstance(){
        init();
        return instance;
    }
	

	@Override
	public void initializeAgentReactions() {
		// module doesn't react to anything
		
	}

	@Override
	public void agentActivity() {
		System.out.print("Enter Command: ");
		String msg = scanner.nextLine();
		
		AgentMessage a = new AgentMessage();
		
		if(matchWithTrigger(msg, Triggers.jokeTiggers)) {
			a.setTopic(Topics.JOKE);
			sendAgentMessage(a.getRoute(),a);	
		}
		else if(matchWithTrigger(msg, Triggers.greetingTiggers)){
			Greeting.init();
			a.setTopic(Topics.GREETING);
			a.addParam("Data",msg);
			sendAgentMessage(a.getRoute(),a);
		}
		else {
			a.setTopic(Topics.TEXT_OUTPUT);
			a.addParam("Data", "Invalid Command");
			sendAgentMessage(a.getRoute(),a);
		}
		
//		Wait for output
		pause(null);
	}
	
	/**
	 * This method matches the input "Token" with an triggers of an agent
	 * @param token
	 * @param triggers - n number of arguments
	 * @return 
	 */
	private boolean matchWithTrigger(String token,String ...triggers) {
		boolean found = false;
		
		for(String trigger: triggers) {
//			TODO Replace contains with regular expression 
			if(token.toLowerCase().contains(trigger)) {
//				System.out.println("found "+trigger);
				return true;	
			}
		}
		return found;
	}

}
