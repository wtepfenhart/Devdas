import java.util.ArrayList;

/**
 *
 * @file CommandLineArgs.java
 * @author wtepfenhart
 * @date: May 29, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */

import java.util.Map;
import java.util.HashMap;

/**
 * @author wtepfenhart
 * This class has some serious limitations. Need to replace this ASAP.
 *
 */
public class CommandLineArgs {

    protected ArrayList arguments;
    protected Map clArgs;
    
    public CommandLineArgs(String[] args)
    {
    	arguments = new ArrayList();
    	clArgs = new HashMap();
    	
        parse(args);
    }
    
    /**
     * 
     * @param args
     */
    public void parse(String[] args)
    {
    	for (String a :args) arguments.add(a);
    }
    
    // the exxample that I initially followed had this function
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
    
    public String valueOf(String opt)
    {
        String value = null;
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
