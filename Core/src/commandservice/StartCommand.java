package commandservice;

public class StartCommand implements CommandProcessor
{
	private static DevdasCore app;

	public StartCommand(DevdasCore program)
	{
		app = program;
	}
	
	public void execute(CommandMessage command)
	{
		app.start(command);
	}
}
