package commandservice;

public class QuitCommandProcessor implements CommandProcessor
{
	public void execute(GenericProg prog, CommandServiceMessage command)
	{
		command.setResponse("Success");
		command.setExplanation("Received Quit Command"); //Probably should remove
		prog.setRunning(false);
	}
}