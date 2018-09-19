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
	 * Calls upon the {@link #process(CommandServiceMessage)} method of a CommandProcessor and handles any exceptions that are thrown. This method should determine the success of a command's processing by using a try-catch block.
	 * If the command is successfully processed or there is an exception of any sort, this method should return the appropriate response within the response field of the
	 * CommandService message implemented through this method.
	 * 
	 * </p>
	 * This method should be implemented by an abstract CommandProcessor and invoked by a GenericProg
	 * 
	 * @param command The CommandService message that contains the command details
	 */
	public void execute(CommandServiceMessage command);
	
	/**
	 * Invokes an individual command either through the program or within the processor itself. This method should attempt to process the command by calling upon the necessary
	 * method within the program or processor. If the command cannot be processed, this method should throw an exception (which is to be caught by the {@link #execute(CommandServiceMessage)} method.
	 * If the command can be processed and needs to return some data, the data should be passed through the explanation field of the CommandService message implemented through this method.
	 * 
	 * </p>
	 * This method should be implemented by a concrete CommandProcessor and invoked by an abstract CommandProcessor
	 * 
	 * @param command The CommandService message that contains the command details
	 * @throws Exception Throws an Exception if the command cannot be processed
	 */
	public void process(CommandServiceMessage command) throws Exception;
}