package commandservice;

/**
 * CommandProcessor that handles returning the current state of the program. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class StatusCommandProcessor implements CommandProcessor
{
	@Override
	public void execute(GenericProg program, CommandServiceMessage command)
	{
		try
		{
			command.setExplanation("ALIVE: " + program.isRunning() + " OPERATIONAL: " + program.isOperational());
			command.setResponse("Success");
		}
		catch (Exception e)
		{
			command.setResponse("Failure");
			command.setExplanation(e.toString());
		}
	}
}