package commandservice;

/**
 * CommandProcessor that handles ending a program's thread. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class QuitCommandProcessor implements CommandProcessor
{
	@Override
	public void execute(GenericProg program, CommandServiceMessage command)
	{
		try
		{
			program.setRunning(false);
			command.setResponse("Success");
			command.setExplanation("Received Quit Command"); //Self-explanatory; probably should replace with something more meaningful
		}
		catch(Exception e)
		{
			command.setResponse("Failure");
			command.setResponse(e.toString());
		}
	}
}