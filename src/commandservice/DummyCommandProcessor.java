package commandservice;

public class DummyCommandProcessor extends OperationCommandProcessor
{
	public DummyCommandProcessor()
	{
		super();
	}
	
	public void process() throws Exception
	{
		System.err.println("DOING SOMETHING");
		sleep(10000);
			
		command.setResponse("Success");
		command.setExplanation("Received Do Command");
	}
}
