package commandservice;

/**
 * CommandProcessor that handles ending a program's thread. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class StopCommand implements CommandProcessor
{
	private static DevdasCore app;

	public StopCommand(DevdasCore program)
	{
		app = program;
	}
	
	public void execute(CommandMessage command)
	{
		app.stop(command);
	}
}