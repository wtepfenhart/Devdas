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
public abstract class OperationCommandProcessor implements CommandProcessor
{
	private DevdasCore program;
	private Thread processingThread;
	
	public OperationCommandProcessor(DevdasCore program)
	{
		this.program = program;
		this.processingThread = new Thread(this.getClass().toString());
	}
	
	public final void execute(CommandServiceMessage command)
	{	
		try
		{
			process(command);
		}
		catch(Exception e)
		{
			command.addParam("Response", "Failure");
			command.addParam("Explanation", e.toString());
		}
	}

	/**
	 * @return Returns the reference to the GenericProgram of this processor
	 */
	public DevdasCore getProgram()
	{
		return program;
	}
	
	public Thread getProcessingThread()
	{
		return processingThread;
	}
}
