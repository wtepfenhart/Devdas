package commandservice;

/**
 * Generic interface for processing commands issued by any CommandService message. All commands issued to a GenericProg should implement this interface.
 * When processing commands, a CommandProcessor should determine if the command is successful and return an appropriate response and explanation.
 * 
 * @author B-T-Johnson
 */

public interface CommandProcessor
{	
	/**
	 * Handles the processing of a command issues by a CommandService message. This method should determine the success of a command by using a try-catch block.
	 * If the command is successfully processed or there is an exception of any sort, this method should return an appropriate response and explanation through the
	 * CommandService message implemented through it.
	 * 
	 * @param command The CommandService message that contains the command details
	 */
	public void execute(CommandServiceMessage command);
}