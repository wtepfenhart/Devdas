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
public class ExitCommand implements CommandProcessor
{
	DevdasCore app;
	
	public ExitCommand(DevdasCore program)
	{
		app = program;
	}
	
	public void execute(CommandMessage command)
	{
		app.exit(command);
	}
}
