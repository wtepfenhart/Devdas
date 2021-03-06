package devdas;
/**
 *
 * @file CommandLineArgs.java
 * @author wtepfenhart
 * @date: May 29, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */

import java.util.ArrayList;

/**
 * @author wtepfenhart
 * This class has some serious limitations. Need to replace this ASAP.
 *
 */
public class CommandLineArgs {

	protected ArrayList<String> arguments;

	public CommandLineArgs(String[] args)
	{
		arguments = new ArrayList<String>();

		parse(args);
	}

	/**
	 * 
	 * @param args - command line args
	 */
	public void parse(String[] args)
	{
		for (String a :args) arguments.add(a);
	}

	// the example that I initially followed had this function
	// unfortunately once I made the modifications that were necessary for
	// this application, the function was basically useless
	public boolean hasOption(String opt)
	{
		String str;
		for (Object  a: arguments) {
			str = (String) a;
			if ( true == str.equalsIgnoreCase(opt) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param opt - the option key to search for
	 * @return
	 */
	public String valueOf(String opt)
	{
		String str;
		for ( int i = 0; i < arguments.size(); i++ ) {
			str = (String)arguments.get(i);
			if ( true == str.equalsIgnoreCase(opt) ) {
				return (String)arguments.get(i+1);
			}
		}
		return null;
	}


}
