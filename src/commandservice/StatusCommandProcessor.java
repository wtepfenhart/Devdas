package commandservice;

/**
 * CommandProcessor that handles returning the current state of the program. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class StatusCommandProcessor extends SystemCommandProcessor
{	
	public StatusCommandProcessor(GenericProg program)
	{
		super(program);
	}
	
	@Override
	public void process() throws Exception
	{
		if(command.getExplanation() != null)
		{
			if (command.getExplanation().toUpperCase().equals("ALL"))
			{
				//TODO Handle "Status All" Case
			}
			else
			{
				SystemCommandProcessor sys = (SystemCommandProcessor) getProgram().getCommands().get("System").get(command.getExplanation().toUpperCase()); //There has got to be a better way...
				OperationCommandProcessor opt = (OperationCommandProcessor) getProgram().getCommands().get("Operation").get(command.getExplanation().toUpperCase()); // " "

				if(sys != null)
				{
					getProgram().sendLogMessage("Status", sys.toString() + " is " + (sys.isAlive() ? "ALIVE" : sys.isInterrupted() ? "INTERRUPTED" : "DEAD") + " and " + sys.getState().toString(), "Info"); //Should we log the status of the processor?

					command.setExplanation(sys.getState().toString());
				}
				else if(opt != null)
				{
					getProgram().sendLogMessage("Status", opt.toString() + " is " + (opt.isAlive() ? "ALIVE" : opt.isInterrupted() ? "INTERRUPTED" : "DEAD") + " and " + opt.getState().toString(), "Info");

					command.setExplanation(opt.getState().toString());
				}
				else
				{
					throw new Exception("Unexpected command: " + command.getExplanation());
				}
			}
		}
		else
		{
			throw new Exception("No command");
		}
	}
}