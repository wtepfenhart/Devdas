package commandservice;

/**
 * CommandProcessor that handles allowing the program to process commands after it has been paused. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class ResumeCommand implements CommandProcessor
{
	private DevdasCore app;

	public ResumeCommand(DevdasCore program)
	{
		app = program;
	}

	public void execute(CommandMessage command)
	{
		app.resume(command);
	}
}