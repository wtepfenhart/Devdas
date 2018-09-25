/**
 * 
 */


import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author wtepfenhart
 *
 */
public class TextInput {

	/**
	 * 
	 */
	public TextInput() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * the following is for testing junit testing (can you say recursion?)
	 */
	public void printHello() {
		System.out.println("Hello Cruel World");
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
