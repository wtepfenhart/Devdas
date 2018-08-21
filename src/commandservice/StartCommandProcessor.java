package commandservice;

/**
 * CommandProcessor that handles allowing the program so that it can process operational commands. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class StartCommandProcessor implements CommandProcessor
{
	@Override
	public void execute(GenericProg prog, CommandServiceMessage command)
	{
		try
		{
			if (!prog.isOperational())
			{
				command.setResponse("Success");
				command.setExplanation("Received Start Command");
				prog.setOperational(true);
			}
			else
			{
				throw new Exception("Program already started!");
			}
		}
		catch(Exception e)
		{
			command.setResponse("Failure");
			command.setExplanation(e.getMessage());
		}
	}
}