package commandservice;

public class QuitCommandProcessor implements CommandProcessor
{
	@Override
	public void execute(GenericProg program, CommandService command)
	{
		System.exit(1);
	}
}