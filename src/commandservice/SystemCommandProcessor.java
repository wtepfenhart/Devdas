package commandservice;

/**
 * Abstract CommandProcessor class for system-level commands; not to be confused with OperationCommandProcessors.
 * These commands handle control commands, such as restart, stop, report, etc, which is used to get individual agents to
 * fulfill system level needs. Any processor that requires access to a program's variables should extend from this class.
 * 
 * </p>
 * Made this extend Thread so that it can operate on a command without blocking any other functionality
 * 
 * @author B-T-Johnson
 */
public abstract class SystemCommandProcessor extends Thread implements CommandProcessor
{
	private GenericProg program;
	protected CommandServiceMessage command;
	
	public SystemCommandProcessor(GenericProg program)
	{
		this.program = program;
	}
	
	public void execute(CommandServiceMessage command)
	{	
		this.command = command;
		
		run(); //Probably should catch any exception here instead of in individual processors
	}
	
	/**
	 * @return Returns the reference to the GenericProgram of this processor
	 */
	public GenericProg getProgram()
	{
		return this.program;
	}
}