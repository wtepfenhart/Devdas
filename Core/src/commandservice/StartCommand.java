package commandservice;

public class StartCommand implements CommandProcessor
{
	private DevdasCore app;

	public StartCommand(DevdasCore program)
	{
		app = program;
	}
	
	public void execute(CommandMessage command)
	{
		app.start(command);
	}
}
