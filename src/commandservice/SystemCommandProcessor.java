package commandservice;

/**
 * Abstract class denoting system-level commands. All system-level commands should extend from this class.
 * 
 * @author B-T-Johnson
 */
public abstract class SystemCommandProcessor implements CommandProcessor //Do we need this class? How should a system command be unique from other commands (like operation commands)?
																		 //Maybe the difference lies in if/what commands are registered?
{
	protected GenericProg prog;
	
	public SystemCommandProcessor(GenericProg program)
	{
		this.prog = program;
	}
	
	@Override
	public abstract void execute(CommandServiceMessage command);
}