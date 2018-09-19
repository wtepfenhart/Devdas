package commandservice;

public class PerformDoCommandProcessor extends OperationCommandProcessor
{
	public PerformDoCommandProcessor(GenericProg program)
	{
		super(program);
	}
	
	public void process(CommandServiceMessage command) throws Exception
	{
		this.getProgram().doSomething();
		
		command.setExplanation("Received Do Command");
	}
}
