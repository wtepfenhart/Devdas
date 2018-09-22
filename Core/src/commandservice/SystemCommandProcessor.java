package commandservice;

/**
 * Abstract CommandProcessor class for system-level commands; not to be confused with OperationCommandProcessors.
 * These commands handle control commands, such as restart, stop, report, etc, which is used to get individual agents to
 * fulfill system level needs. Any processor that handles a program's functionality should extend from this class.
 * 
 * </p>
 * Made this extend Thread so that it can operate on a command without blocking any other functionality
 * 
 * @author B-T-Johnson
 */
public abstract class SystemCommandProcessor implements CommandProcessor
{
	private DevdasCore program;
	private Thread processingThread;
	
	public SystemCommandProcessor(DevdasCore program)
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