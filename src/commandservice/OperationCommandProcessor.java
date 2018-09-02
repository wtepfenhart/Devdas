package commandservice;

public abstract class OperationCommandProcessor extends Thread implements CommandProcessor
{
	protected CommandServiceMessage command;
	
	public OperationCommandProcessor()
	{
		this.setName(this.getClass().toString());
	}
	
	public void execute(CommandServiceMessage command)
	{	
		this.command = command;
		
		run(); //Probably should catch any exception here
	}
}
