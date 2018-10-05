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
	
	/**
	 * the following is for testing junit testing (can you say recursion?)
	 */
	public void printHello() {
		System.out.println("Hello Cruel World");
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TextInput t = new TextInput();
		try {
			System.out.println(t.getClass().getSimpleName() + " " + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
