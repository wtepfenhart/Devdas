package commandservice;

/**
 * CommandProcessor that handles temporarily stopping a program from processing any further operation commands. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class PauseCommandProcessor implements CommandProcessor
{
	@Override
	public void execute(GenericProg program, CommandServiceMessage command)
	{
		try
		{
			if(program.isOperational())
			{
				program.setOperational(false);
				command.setResponse("Success");
				command.setExplanation("Received Pause Command"); //Self-explanatory; probably should replace with something more meaningful
			}
			else
			{
				throw new Exception("Program already paused!"); //Cannot pause a program twice
			}
		}
		catch(Exception e)
		{
			command.setResponse("Failure");
			command.setExplanation(e.toString());
		}
	}
}