package commandservice;

/**
 * CommandProcessor that handles ending a program's thread. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class StopCommandProcessor extends SystemCommandProcessor
{
	public StopCommandProcessor(GenericProg program)
	{
		super(program);
	}

	@Override
	public void run()
	{
		try
		{
			if(getProgram().isRunning())
			{
				getProgram().setRunning(false);
				command.setResponse("Success");
				command.setExplanation("Received Stop Command"); //Self-explanatory; probably should replace with something more meaningful
			}
			else
			{
				throw new Exception("Cannot Stop " + getProgram().toString());
			}
		}
		catch(Exception e)
		{
			command.setResponse("Failure");
			command.setResponse(e.toString());
		}
	}
}