package commandservice;

/**
 * CommandProcessor that handles returning the current state of the program. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class PerformStatusCommandProcessor extends SystemCommandProcessor
{	
	public PerformStatusCommandProcessor(GenericProg program)
	{
		super(program);
	}
	
	public void process(CommandServiceMessage command) throws Exception
	{
		if(command.getExplanation() != null)
		{
			if (command.getExplanation().toUpperCase().equals("ALL"))
			{
				//TODO Handle "Status All" Case
			}
			else
			{
				command.setExplanation(getProgram().status(command.getExplanation()).toString());
			}
		}
		else
		{
			throw new Exception("No command");
		}
	}
}