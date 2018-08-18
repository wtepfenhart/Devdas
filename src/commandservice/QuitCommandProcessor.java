package commandservice;

public class QuitCommandProcessor extends SystemCommandProcessor
{	
	public QuitCommandProcessor(GenericProg prog)
	{
		super(prog);
	}
	
	public void execute(CommandServiceMessage command)
	{
		command.setResponse("Success");
		command.setExplanation("Received Quit Command");
		prog.setRunning(false);
	}
}