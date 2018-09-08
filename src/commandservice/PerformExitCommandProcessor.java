package commandservice;

/**
 * CommandProcessor that handles exiting from the application. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * </p>
 * Note that if the command is successful, no success message can be retrieved, since the whole application would be effectively terminated.
 * 
 * @author B-T-Johnson
 */
public class PerformExitCommandProcessor extends SystemCommandProcessor
{
	public PerformExitCommandProcessor(GenericProg program)
	{
		super(program);
	}
	
	public void process(CommandServiceMessage command) throws Exception
	{
		getProgram().exit();
		
		command.setExplanation("Received Exit Command"); //Unnecessary, since the command will not be read after exiting
	}
}
