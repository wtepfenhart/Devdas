/**
 * Author: wtepfenhart
 * File: TextInputTest.java
 * Date: Sep 24, 2018
 */

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.*;
/**
 * @author wtepfenhart
 *
 */
class TextInputTest {
	private static ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private static ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private static final PrintStream originalOut = System.out;
	private static final PrintStream originalErr = System.err;

	@BeforeAll
	public static void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}

	@AfterAll
	public static void restoreStreams() {
	    System.setOut(originalOut);
	    System.setErr(originalErr);
	}

	@BeforeEach
	public void clearStreams() {
		outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
	}
	/**
	 * Test method for {@link TextInput#TextInput()}.
	 */
	@Test
	void testTextInput() {
		TextInput t = new TextInput();
		if (t == null) fail("Not yet implemented");		
	}

	@Test
	void isWorking() {
		if (System.out == originalOut) {
				System.out.println("It didn't change a thing!");
				fail("Didn't work!");
		}
	}


    
	/**
	 * Test method for {@link TextInput#printHello()}.
	 */
	@Test
	void testPrintHello1() {
		TextInput t = new TextInput();
		t.printHello();
	    assertNotEquals("hello world\n", outContent.toString());
	}
	

	/**
	 * Test method for {@link TextInput#printHello()}.
	 */
	@Test
	void testPrintHello2() {
		TextInput t = new TextInput();
		t.printHello();
	    assertEquals("Hello Cruel World\n", outContent.toString());
	}

}
