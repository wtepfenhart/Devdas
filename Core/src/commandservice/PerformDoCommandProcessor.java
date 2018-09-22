package commandservice;

public class PerformDoCommandProcessor extends OperationCommandProcessor
{
	public PerformDoCommandProcessor(DevdasCore program)
	{
		super(program);
	}
	
	public void process(CommandServiceMessage command) throws Exception
	{
		getProgram().doSomething(command);
	}
}
