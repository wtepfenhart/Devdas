package commandservice;

public class DummyCommandProcessor extends OperationCommandProcessor
{
	public DummyCommandProcessor()
	{
		super();
	}
	
	@Override
	public void run()
	{
		try
		{
			System.err.println("DOING SOMETHING");
			sleep(100000);
			
			command.setResponse("Success");
			command.setExplanation("Received Do Command");
		}
		catch(Exception e)
		{
			command.setResponse("Failure");
			command.setExplanation(e.toString());
		}
	}
}
