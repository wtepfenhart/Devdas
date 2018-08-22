package commandservice;

public class PauseCommandProcessor implements CommandProcessor
{
	@Override
	public void execute(GenericProg program, CommandServiceMessage command)
	{
		try
		{
			if(program.isOperational())
			{
				command.setResponse("Success");
				command.setExplanation("Received Pause Command");
				program.setOperational(false);
			}
			else
			{
				throw new Exception("Program already paused!"); //Cannot pause a program twice
			}
		}
		catch(Exception e)
		{
			command.setResponse("Failure");
			command.setExplanation(e.getMessage());
		}
	}
}