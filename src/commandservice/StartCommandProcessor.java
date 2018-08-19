package commandservice;

public class StartCommandProcessor implements CommandProcessor
{
	@Override
	public void execute(GenericProg prog, CommandServiceMessage command)
	{
		command.setResponse("Success");
		command.setExplanation("Received Start Command");
		prog.setOperational(true);
	}
}