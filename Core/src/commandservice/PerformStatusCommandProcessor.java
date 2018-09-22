package commandservice;

/**
 * CommandProcessor that handles returning the current state of the program. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class PerformStatusCommandProcessor extends SystemCommandProcessor
{	
	public PerformStatusCommandProcessor(DevdasCore program)
	{
		super(program);
	}
	
	public void process(CommandServiceMessage command) throws Exception
	{
		if(command.getParam("Explanation") != null)
		{
			getProgram().status(command);
		}
		else
		{
			throw new Exception("No command");
		}
	}
}