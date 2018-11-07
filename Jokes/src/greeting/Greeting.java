/**	
 * @author Hamza Zafar
 * This is the Greeting skill.  It listens for the GREETINGS Topic and send a reply(AgentMessage) to TextOutput. 
 */

package greeting;

import commandservice.AgentMessage;
import commandservice.AgentReaction;
import commandservice.DevdasCore;
import devdas.Configuration;
import globals.Globals;
import globals.Topics;

public class Greeting extends DevdasCore {

	private static Greeting instance;
    
	private Greeting(Configuration config) {
		super(config);
	}
	
    public static synchronized Greeting getInstance(String[] args){
        init();
        return instance;
    }
    
    public static synchronized void init(){
        if(instance == null){
        	System.out.println("Made greeting agent instance.");
        	Configuration config = new Configuration(Globals.args);
            instance = new Greeting(config);
        }
    }

	@Override
	public void initializeAgentReactions() {
		agentInterests.add(Topics.GREETING);
		agentReactions.put(Topics.GREETING, new Response("Greetings Earthling"));

	}

	public class Response implements AgentReaction{

		private String response = null;
		/**
		 * Default Constructor
		 */
		public Response(String s) {
			this.response = s;
		}	

		/**
		 * Simple print line to a console
		 */
		public void execute(AgentMessage msg) {
			AgentMessage a = new AgentMessage();
			a.addParam("Data", response);
			a.setTopic(Topics.TEXT_OUTPUT);
			sendAgentMessage(a.getRoute(),a);
		}
	}
	
	@Override
	public void agentActivity() {}

}
