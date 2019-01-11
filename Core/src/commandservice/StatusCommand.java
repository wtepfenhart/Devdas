package commandservice;

/**
 * CommandProcessor that handles returning the current state of the program. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class StatusCommand implements CommandProcessor
{	
	private static DevdasCore app;
	
	public StatusCommand(DevdasCore program)
	{
		app = program;
	}
	
	public void execute(CommandMessage command)
	{
		app.status(command);
	}
}