package commandservice;

public class StatusCommandProcessor implements CommandProcessor {

	@Override
	public void execute(GenericProg program, CommandServiceMessage command)
	{
		command.setResponse("Success");
		command.setExplanation(program.getState().toString());
	}
}