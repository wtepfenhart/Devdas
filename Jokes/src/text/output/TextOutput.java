/**
 * @author Hamza Zafar	
 * This class listens for TEXT_OUTPUT Topic, and prints out whatever message the AgentMessage object had.
 */

package text.output;

import java.io.PrintStream;

import commandservice.AgentMessage;
import commandservice.AgentReaction;
import commandservice.DevdasCore;
import devdas.Configuration;
import globals.Globals;
import globals.Topics;
import text.input.TextInput;

public class TextOutput extends DevdasCore {

	private static TextOutput instance; 
	private PrintStream output = System.out;
	
	public static synchronized TextOutput getInstance(){
        init();
        return instance;
    }
    
    public static synchronized void init(){
        if(instance == null){
        	System.out.println("Made text output agent instance.");
        	Configuration config = new Configuration(Globals.args);
            instance = new TextOutput(config);
        }
    }
    
	private TextOutput(Configuration config) {
		super(config);
	}

	@Override
	public void initializeAgentReactions() {
		agentInterests.add(Topics.TEXT_OUTPUT);
		agentReactions.put(Topics.TEXT_OUTPUT, new Output());

	}

	public class Output implements AgentReaction{

		/**
		 * Default Constructor
		 */
		public Output() {}	

		/**
		 * Print output
		 */
		public void execute(AgentMessage cmd) {
			output.println(cmd.getParam("Data"));
			TextInput.getInstance().resume(null);
		}
	}
	
	@Override
	public void agentActivity() {
		// TODO Auto-generated method stub

	}

}
