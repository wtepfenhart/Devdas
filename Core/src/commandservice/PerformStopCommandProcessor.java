package commandservice;

/**
 * CommandProcessor that handles ending a program's thread. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class PerformStopCommandProcessor extends SystemCommandProcessor
{
	public PerformStopCommandProcessor(DevdasCore program)
	{
		super(program);
	}
	
	public void process(CommandServiceMessage command) throws Exception
	{
		getProgram().stop(command);
	}
}