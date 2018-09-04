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
	
	public void process() throws Exception
	{
		if(getProgram().isRunning())
		{
			getProgram().setRunning(false);
		}
		else
		{
			throw new Exception("Cannot Stop " + getProgram().toString());
		}
		
		command.setExplanation("Received Stop Command"); //Self-explanatory; probably should replace with something more meaningful
	}
}