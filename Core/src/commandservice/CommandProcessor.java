package commandservice;

/**
 * Generic interface for handling commands issued by any CommandService message. All commands issued to a GenericProg should implement this interface.
 * When processing commands, a CommandProcessor should determine if the command is successful and return an appropriate response and explanation.
 * 
 * @author B-T-Johnson
 */

public interface CommandProcessor
{	
	/**
	 * Calls upon the {@link #process(CommandMessage)} method of a CommandProcessor and handles any exceptions that are thrown. This method should determine the success of a command's processing by using a try-catch block.
	 * If the command is successfully processed or there is an exception of any sort, this method should return the appropriate response within the response field of the
	 * CommandService message implemented through this method.
	 * 
	 * </p>
	 * This method should be implemented by an abstract CommandProcessor and invoked by a GenericProg
	 * 
	 * @param command The CommandService message that contains the command details
	 */
	public void execute(CommandMessage command);
	
}