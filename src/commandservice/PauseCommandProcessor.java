package commandservice;

/**
 * CommandProcessor that handles temporarily stopping a program from processing a command. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class PauseCommandProcessor extends SystemCommandProcessor
{	
	public PauseCommandProcessor(GenericProg program)
	{
		super(program);
	}
	
	@Override
	public void process() throws Exception
	{
		if(command.getExplanation() != null)
		{
			if(command.getExplanation().toUpperCase().equals("ALL"))
			{
				//TODO Handle "Pause All" Case
			}
			else
			{
				SystemCommandProcessor sys = (SystemCommandProcessor) getProgram().getCommands().get("System").get(command.getExplanation().toUpperCase()); //There has got to be a better way...
				OperationCommandProcessor opt = (OperationCommandProcessor) getProgram().getCommands().get("Operation").get(command.getExplanation().toUpperCase()); // " "

				if(sys != null)
				{	
					if(sys.getState().equals(State.RUNNABLE))
					{
						sys.suspend(); //TODO Need to fix
					}
					else
					{
						throw new Exception("Cannot pause " + sys.toString()); //Cannot pause a processor that isn't running
					}
					
					command.setExplanation(sys.toString() + " received Pause Command"); //Self-explanatory; probably should replace with something more meaningful
				}
				else if(opt != null)
				{	
					if(opt.getState().equals(State.RUNNABLE))
					{
						opt.suspend();					
					}
					else
					{
						throw new Exception("Cannot pause " + opt.toString()); //Cannot pause a processor that isn't running
					}
					
					command.setExplanation(opt.toString() + " received Pause Command"); //Self-explanatory; probably should replace with something more meaningful
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