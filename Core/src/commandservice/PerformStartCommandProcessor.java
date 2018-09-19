package commandservice;

public class PerformStartCommandProcessor extends SystemCommandProcessor
{
	public PerformStartCommandProcessor(GenericProg program)
	{
		super(program);
	}
	
	public void process(CommandServiceMessage command) throws Exception
	{
		getProgram().start();
		
		command.setExplanation("Received Resume Command"); //Self-explanatory; probably should replace with something more meaningful
	}
}
