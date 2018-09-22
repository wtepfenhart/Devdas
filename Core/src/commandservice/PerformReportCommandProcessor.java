package commandservice;

public class PerformReportCommandProcessor extends SystemCommandProcessor
{
	public PerformReportCommandProcessor(DevdasCore program)
	{
		super(program);
	}

	public void process(CommandServiceMessage command) throws Exception
	{	
		getProgram().report(command);
	}
}
