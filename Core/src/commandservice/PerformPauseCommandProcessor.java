package commandservice;

/**
 * CommandProcessor that handles temporarily stopping a program from processing a command. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class PerformPauseCommandProcessor extends SystemCommandProcessor
{	
	public PerformPauseCommandProcessor(DevdasCore program)
	{
		super(program);
	}
	
	public void process(CommandServiceMessage command) throws Exception
	{
		if(command.getParam("Explanation") != null)
		{
			getProgram().pause(command);
		}
		else
		{
			throw new Exception("No command");
		}
	}
}