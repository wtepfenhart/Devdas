package commandservice;

/**
 * CommandProcessor that handles sending a log message to the appropriate log exchange. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class PerformSetLogLevelCommandProcessor extends SystemCommandProcessor
{	
	public PerformSetLogLevelCommandProcessor(DevdasCore program)
	{
		super(program);
	}

	public void process(CommandServiceMessage command) throws Exception
	{
		if(command.getParam("Explanation") != null)
		{	
			getProgram().setLogLevel(command);
		}
		else
		{
			throw new Exception("No command");
		}
	}
}