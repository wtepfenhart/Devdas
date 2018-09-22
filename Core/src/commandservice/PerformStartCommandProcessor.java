package commandservice;

public class PerformStartCommandProcessor extends SystemCommandProcessor
{
	public PerformStartCommandProcessor(DevdasCore program)
	{
		super(program);
	}
	
	public void process(CommandServiceMessage command) throws Exception
	{
		getProgram().start(command);
	}
}
