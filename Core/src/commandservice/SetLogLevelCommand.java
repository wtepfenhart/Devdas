package commandservice;

/**
 * CommandProcessor that handles sending a log message to the appropriate log exchange. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class SetLogLevelCommand implements CommandProcessor
{	
	private DevdasCore app;

	public SetLogLevelCommand(DevdasCore program)
	{
		app = program;
	}

	public void execute(CommandMessage command)
	{
		app.setLogLevel(command);
	}
}