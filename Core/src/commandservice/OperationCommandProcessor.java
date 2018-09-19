package commandservice;

import java.lang.Thread.State;

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
	private GenericProg program;
	private Thread processingThread;
	
	public OperationCommandProcessor(GenericProg program)
	{
		this.program = program;
		this.processingThread = new Thread(this.getClass().toString());
	}
	
	public final void execute(CommandServiceMessage command)
	{	
		try
		{
			processingThread.start();
				process(command);
			processingThread.stop();
			
			command.setResponse("Success");
		}
		catch(Exception e)
		{
			command.setResponse("Failure");
			command.setExplanation(e.toString());
		}
	}

	/**
	 * @return Returns the reference to the GenericProgram of this processor
	 */
	public GenericProg getProgram()
	{
		return program;
	}
	
	public Thread getProcessingThread()
	{
		return processingThread;
	}
}
