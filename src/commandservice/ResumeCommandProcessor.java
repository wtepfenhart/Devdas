package commandservice;

/**
 * CommandProcessor that handles allowing the program to process commands after it has been paused. If the program cannot process the command, then a failure response will be set to the CommandService Message.
 * Otherwise, the processor will return a successful response.
 * 
 * @author B-T-Johnson
 */
public class ResumeCommandProcessor extends SystemCommandProcessor
{
	public ResumeCommandProcessor(GenericProg program)
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
				if(command.getExplanation().toUpperCase() == "ALL")
				{
					//TODO Handle "Start All" Case
				}
				else
				{
					SystemCommandProcessor sys = (SystemCommandProcessor) getProgram().getCommands().get("System").get(command.getExplanation().toUpperCase()); //There has got to be a better way...
					OperationCommandProcessor opt = (OperationCommandProcessor) getProgram().getCommands().get("Operation").get(command.getExplanation().toUpperCase()); // " "
					
					if(sys != null)
					{	
						if(!sys.getState().equals(State.RUNNABLE))
						{
							sys.resume();
							command.setResponse("Success");
							command.setExplanation(sys.toString() + " received Resume Command"); //Self-explanatory; probably should replace with something more meaningful
						}
						else
						{
							throw new Exception("Cannot start " + sys.toString()); //Cannot start a processor that is already running
						}
					}
					else if(opt != null)
					{	
						if(!opt.getState().equals(State.RUNNABLE))
						{
							opt.resume();
							command.setResponse("Success");
							command.setExplanation(opt.toString() + " received Resume Command"); //Self-explanatory; probably should replace with something more meaningful
						}
						else
						{
							throw new Exception("Cannot start " + opt.toString()); //Cannot start a processor that is already running
						}
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
		catch(Exception e)
		{
			command.setResponse("Failure");
			command.setExplanation(e.toString());
		}
	}
}