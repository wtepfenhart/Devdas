package commandservice;

/**
 * Generic interface for commands issued by any CommandService object. All commands should implement this interface.
 * 
 * @author B-T-Johnson
 */

public interface CommandProcessor
{	
	public void execute(CommandServiceMessage command);
}