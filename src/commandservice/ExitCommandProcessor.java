package commandservice;

public class ExitCommandProcessor implements CommandProcessor {

	@Override
	public void execute(GenericProg program, CommandServiceMessage command)
	{
		command.setResponse("Success");
		command.setExplanation("Received Exit Command");
		System.exit(1);
	}

}
