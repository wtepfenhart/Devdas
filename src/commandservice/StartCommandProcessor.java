package commandservice;

public class StartCommandProcessor extends SystemCommandProcessor
{
	public StartCommandProcessor(GenericProg program)
	{
		super(program);
	}
	
	public void run()
	{
		try
		{
			if(getProgram().isRunning())
			{
				throw new Exception("Cannot Start " + getProgram().toString()); //Cannot start a program that is already running
			}
			else
			{
				getProgram().setRunning(true);
				command.setResponse("Success");
				command.setExplanation("Received Resume Command"); //Self-explanatory; probably should replace with something more meaningful
			}
		}
		catch(Exception e)
		{
			command.setResponse("Failure");
			command.setExplanation(e.toString());
		}
	}
}
