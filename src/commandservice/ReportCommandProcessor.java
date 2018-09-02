package commandservice;

public class ReportCommandProcessor extends SystemCommandProcessor
{
	public ReportCommandProcessor(GenericProg program)
	{
		super(program);
	}

	@Override
	public void run()
	{
		try
		{
			getProgram().sendLogMessage("Report", getProgram().toString() + " is " + (getProgram().isRunning() ? "RUNNING" : "NOT RUNNING"), "Info"); //TODO Refactor "not running" to something more useful
			command.setResponse("Success");
			command.setExplanation(getProgram().isRunning() ? "RUNNING" : "NOT RUNNING");
		}
		catch(Exception e)
		{
			command.setResponse("Failure");
			command.setExplanation(e.toString());
		}
	}
}
