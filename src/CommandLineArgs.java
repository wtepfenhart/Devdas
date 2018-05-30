import java.util.ArrayList;

/**
 *
 * @file CommandLineArgs.java
 * @author wtepfenhart
 * @date: May 29, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */



/**
 * @author wtepfenhart
 * This class has some serious limitations. Need to replace this ASAP.
 *
 */
public class CommandLineArgs {

    protected ArrayList arguments;
    
    public CommandLineArgs(String[] args)
    {
    	arguments = new ArrayList();
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
