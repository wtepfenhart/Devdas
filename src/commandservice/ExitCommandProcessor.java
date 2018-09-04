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
public class ExitCommandProcessor extends SystemCommandProcessor
{
	public ExitCommandProcessor(GenericProg program)
	{
		super(program);
	}
	
	public void process() throws Exception
	{
		System.exit(1);
		
		command.setExplanation("Received Exit Command"); //Unnecessary, since the command will not be read after exiting
	}
}
