package commandservice;

public class QuitCommandProcessor implements CommandProcessor
{
	@Override
	public void execute()
	{
		System.exit(1);
	}
}