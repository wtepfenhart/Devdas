package commandservice;

/**
 * CommandProcessor that handles sending a log message to the appropriate log exchange. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class LogCommandProcessor extends SystemCommandProcessor
{	
	public LogCommandProcessor(GenericProg program)
	{
		super(program);
	}

	@Override
	public void run()
	{
		try
		{	
			if(command.getExplanation() != null)
			{	
				getProgram().sendLogMessage("Message", command.getExplanation(), "Info"); //Should there be an option to set the event name and severity level to something other than "Message" and "Info"?
			}
			else
			{
				throw new Exception("No message set to log command!");
			}
			
			command.setResponse("Success");
			command.setExplanation("Received Log Command"); //Self-explanatory; probably should replace with something more meaningful
		}
		catch(Exception e)
		{
			command.setResponse("Failure");
			command.setExplanation(e.toString());
		}
	}
}