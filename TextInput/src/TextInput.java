/**
 * @author wtepfenhart
 *Nicholas-Jason Roache
 *
 */

import java.io.IOException;
public class TextInput {

	//Main variables
	static String kind;
	static String value;
	static String input;
	static String interpreter;
	//constants
	private final static String QUEUE_NAME = "Request";
	/**
	 * 
	 */
	public TextInput() {
		// TODO Auto-generated constructor stub
		kind = null;
		value = null;
		input = null;
		interpreter = null;
	}

	public TextInput(String kind, String value, String input, String interpreter)
	{
		kind = this.kind;
		value = this.value;
		input = this.input;
		interpreter = this.interpreter;
	}

	//Intial setters and getters
	public void setKind(String kind)
	{
		this.kind = kind;
	}

	public String getKind()
	{
		return kind;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public void setInput(String input)
	{
		this.input = input;
	}

	public String getInput()
	{
		return input;
	}

	public void setInterpreter(String interpreter)
	{
		this.interpreter = interpreter;
	}

	public String getInterpreter()
	{
		return interpreter;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// Aside from message everything may be temporary
	ConnectionFactory factory = new ConnectionFactory();
	factory.setHost("localhost");
	Connection connection = factory.newConnection();
	Channel channel = connection.createChannel();

	channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	String message = "Kind: " + kind +", Value : " + value + "Input " + input + "Interpreter: " + interpreter;
	channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
	connection.close();
	channel.close();
	}

}
