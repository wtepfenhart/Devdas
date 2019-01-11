package commandservice;

/**
 * CommandProcessor that handles temporarily stopping a program from processing a command. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class PauseCommand implements CommandProcessor
{	
	private static DevdasCore app;
	
	public PauseCommand(DevdasCore program)
	{
		app = program;
	}
	
	public void execute(CommandMessage command)
	{
		app.pause(command);
	}
}