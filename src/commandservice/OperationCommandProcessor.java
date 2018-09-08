package commandservice;

/**
 * Abstract CommandProcessor class for operation commands; not to be confused with SystemCommandProcessors.
 * These commands handle tasks specific to an individual agent that may require additional operations.
 * Any processor that does not requires access to a program's variables should extend from this class.
 * 
 * </p>
 * Made this extend Thread so that it can operate on a command without blocking any other functionality
 * 
 * @author B-T-Johnson
 */
public abstract class OperationCommandProcessor extends Thread implements CommandProcessor
{	
	public OperationCommandProcessor()
	{
		this.setName(this.getClass().toString());
	}
	
	public final void execute(CommandServiceMessage command)
	{	
		try
		{
			process(command);
			command.setResponse("Success");
		}
		catch(Exception e)
		{
			command.setResponse("Failure");
			command.setExplanation(e.toString());
		}
	}
}
