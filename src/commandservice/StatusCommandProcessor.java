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
			command.setResponse("Success");
			command.setExplanation("STATE: " + program.getState().toString() + " ALIVE: " + program.isAlive() + " OPERATIONAL: " + program.isOperational());
		}
		catch (Exception e)
		{
			command.setResponse("Failure");
			command.setExplanation(e.getMessage());
		}
	}
}