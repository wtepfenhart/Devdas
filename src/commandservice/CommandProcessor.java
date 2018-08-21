package commandservice;

/**
 * Generic interface for processing commands issued by any CommandService message. All commands issued to a GenericProg should implement this interface.
 * When processing commands, a CommandProcessor should determine if the command is successful and return an appropriate response and explanation.
 * 
 * @author B-T-Johnson
 */

public interface CommandProcessor
{	
	public void execute(GenericProg program, CommandServiceMessage command);
}