package commandservice;

public class ReportCommand implements CommandProcessor
{
	private DevdasCore app;

	public ReportCommand(DevdasCore program)
	{
		app = program;
	}

	public void execute(CommandMessage command)
	{	
		app.report(command);
	}
}
