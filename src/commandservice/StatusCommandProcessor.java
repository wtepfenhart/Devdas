package commandservice;

public class StatusCommandProcessor extends SystemCommandProcessor
{
	public StatusCommandProcessor(GenericProg prog)
	{
		super(prog);
	}
	
	public void execute(CommandServiceMessage command) //Should this be returning the state (and not just setting it to the explanation)? If so, how to address return type?
	{
		command.setResponse("Success");
		command.setExplanation("Status of " + prog.getClass() + " is: " + prog.getState().toString());
	}
}
