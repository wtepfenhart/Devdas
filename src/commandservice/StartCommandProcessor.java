package commandservice;

public class StartCommandProcessor extends SystemCommandProcessor
{
	public StartCommandProcessor(GenericProg program)
	{
		super(program);
	}
	
	public void process() throws Exception
	{
		if(getProgram().isRunning())
		{
			throw new Exception("Cannot Start " + getProgram().toString()); //Cannot start a program that is already running
		}
		else
		{
			getProgram().setRunning(true);
		}

		command.setExplanation("Received Resume Command"); //Self-explanatory; probably should replace with something more meaningful
	}
}
