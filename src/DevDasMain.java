import java.io.FileInputStream;
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

	    CommandLineArgs cmd = new CommandLineArgs(args);
	    
	    try{
	        Properties p = new Properties();
	        p.load(new FileInputStream("user.ini"));
	        ipAddress = p.getProperty("ipAddress");
	        userName = p.getProperty("UserName");
	        userPassword = p.getProperty("userPassword");
	        virtualHost = p.getProperty("virtualHost");
	        }
	      catch (Exception e) {
	        System.out.println(e);
	        }
	
		if ( cmd.hasOption("-i") ) {
			ipAddress = cmd.valueOf("-i");
		};
		if ( cmd.hasOption("-u") ) {
			userName = cmd.valueOf("-u");
		};
		if ( cmd.hasOption("-p") ) {
			userPassword = cmd.valueOf("-p");
		}; 
			if ( cmd.hasOption("-v") ) {
			virtualHost = cmd.valueOf("-v");;
		};
}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DevDasMain main = new DevDasMain(args);

	}

}
