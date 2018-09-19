package commandservice;

public class PerformReportCommandProcessor extends SystemCommandProcessor
{
	public PerformReportCommandProcessor(GenericProg program)
	{
		super(program);
	}

	public void process(CommandServiceMessage command) throws Exception
	{	
		command.setExplanation(getProgram().report());
	}
}
