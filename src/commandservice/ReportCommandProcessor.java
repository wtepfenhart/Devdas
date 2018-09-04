package commandservice;

public class ReportCommandProcessor extends SystemCommandProcessor
{
	public ReportCommandProcessor(GenericProg program)
	{
		super(program);
	}

	public void process() throws Exception
	{
		getProgram().sendLogMessage("Report", getProgram().toString() + " is " + (getProgram().isRunning() ? "RUNNING" : "NOT RUNNING"), "Info"); //TODO Refactor "not running" to something more useful
		
		command.setExplanation(getProgram().isRunning() ? "RUNNING" : "NOT RUNNING");
	}
}
