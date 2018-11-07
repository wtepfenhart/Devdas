package test;

import globals.Globals;
import text.input.TextInput;
import text.output.TextOutput;

public class Main {
	
	public static void main(String[] args) {
		Globals.args = args;
//		Initialize input and run
		TextInput.init();
		
//		Initialize output
		TextOutput.init();
		
//		Run TextInput Agent
		TextInput.getInstance().run();
	}
}
