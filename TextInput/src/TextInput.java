/**
 * @author wtepfenhart
 *Nicholas-Jason Roache
 *
 */

//import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import commandservice.DevdasCore;
import devdas.Configuration;
public class TextInput extends DevdasCore  {

	//Main variables
	static String kind;
	static String value;
	static String input;
	static String interpreter;
	Scanner scanner;
	private static Configuration config;
	//constants
	private final static String QUEUE_NAME = "Request";
	
	/**
	 * Has random variables to make testing easier
	 */
	public TextInput() {
		// TODO Auto-generated constructor stub
		super(config);
		scanner = new Scanner(System.in);
		kind = "kind";
		value = "value";
		input = "input";
		interpreter = "interpret";
	}

	public TextInput(String kind, String value, String input, String interpreter)
	{
		super(config);
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

	//Initial setters and getters
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
	//abstract methods
	public void agentActivity()
	{
		
	}
	
	@Override
	
	 
	 
	public void initializeAgentReactions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeAgentInterests() {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args)  {
		// TODO Auto-generated method stub
		
		TextInput t = new TextInput();
		System.out.println("Enter the kind:");
		String k = t.scanner.next();
		t.setKind(k);
		System.out.println("Enter the value:");
		String v = t.scanner.next();
		t.setValue(v);
		System.out.println("Enter the input:");
		String i = t.scanner.next();
		t.setInput(i);
		System.out.println("Enter interpreter:");
		String it = t.scanner.next();
		t.setInterpreter(it);
		try {
			System.out.println(t.getClass().getSimpleName() + " " + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}