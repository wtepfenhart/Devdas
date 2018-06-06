import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 */

/**
 * @author wtepfenhart
 *
 */
public class DevDasMain {
	private String ipAddress;
	private String userName;
	private String userPassword;
	private String virtualHost;
	
	

	/**
	 * 
	 */
	public DevDasMain(String[] args) {
		super();
		String current ="";
	    try {
			current = new java.io.File( "." ).getCanonicalPath();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    try{
	    	
	    	// get default values from the user.ini file located in the resources file
	        Properties p = new Properties();
	        p.load(new FileInputStream(current+"/resources/user.ini"));
	        ipAddress = p.getProperty("ipAddress");
	        userName = p.getProperty("userName");
	        userPassword = p.getProperty("userPassword");
	        virtualHost = p.getProperty("virtualHost");
	        }
	      catch (Exception e) {
	        System.out.println(e);
	        }
	    
	    // process command line args if any to overrule defaultss
	    CommandLineArgs cmd = new CommandLineArgs(args);
	    for (int i=0; i < args.length; i++) {
	    	switch (args[i]) {
	    	case "-i":
	    		ipAddress = cmd.valueOf("-i");
	    		i++;
	    		break;
	    	case "-u":
	    		userName = cmd.valueOf("-u");
	    		i++;
	    		break;
	    	case "-p":
	    		userPassword= cmd.valueOf("-p");
	    		i++;
	    		break;
	    	case "-v":
	    		virtualHost = cmd.valueOf("-v");
	    		i++;
	    		break;
	    	default:
	    		System.out.println("Unknown argument: " + args[i] +"\n");
	    		System.out.println("Usage:");
	    		System.out.println("\tDevDasMain [-i address] [-u username] [-p password] [-v virtualhost]");
	    		break;		
	    	
	    	}
	    		
	    }
	    

}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DevDasMain main = new DevDasMain(args);

	}

}
