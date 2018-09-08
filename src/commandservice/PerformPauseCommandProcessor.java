package commandservice;

/**
 * CommandProcessor that handles temporarily stopping a program from processing a command. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class PerformPauseCommandProcessor extends SystemCommandProcessor
{	
	public PerformPauseCommandProcessor(GenericProg program)
	{
		super(program);
	}
	
	@Override
	public void process(CommandServiceMessage command) throws Exception
	{
		if(command.getExplanation() != null)
		{
			if(command.getExplanation().toUpperCase().equals("ALL"))
			{
				//TODO Handle "Pause All" Case
			}
			else
			{
				getProgram().pause(command.getExplanation());
			}
		}
		else
		{
			throw new Exception("No command");
		}
		
		command.setExplanation("Received Pause Command"); //Self-explanatory; probably should replace with something more meaningful
	}
}