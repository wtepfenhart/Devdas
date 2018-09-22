package commandservice;

/**
 * CommandProcessor that handles allowing the program to process commands after it has been paused. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class PerformResumeCommandProcessor extends SystemCommandProcessor
{
	public PerformResumeCommandProcessor(DevdasCore program)
	{
		super(program);
	}

	public void process(CommandServiceMessage command) throws Exception
	{
		if(command.getParam("Explanation") != null)
		{
			getProgram().resume(command);
		}
		else
		{
			throw new Exception("No command");
		}
	}
}