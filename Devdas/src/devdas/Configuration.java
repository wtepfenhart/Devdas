/**
 *
 * @file Configuration.java
 * @author wtepfenhart
 * @date: May 29, 2018
 * Copyright wtepfenhart (c) 2018
 *
 */

package devdas;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * 
 */

/**
 * @author wtepfenhart
 * <p/>
 * This class gets configuration data from two sources: the user.ini file
 * and command line arguments. The settings provided in the user.ini file are overridden
 * by the command line arguments. An instance of this class contains rabbitmq connection
 * information. 
 * <p/> The command line arguments are:
 * <ul style="list-style-type :none">
 * <li> -i	IP Address </li>
 * <li> -u  User name </li>
 * <li> -p  Password </li>
 * <li> -v  Virtual Host </li>
 * <li> -e  Exchange </li>
 * </ul>
 * More to be added at a later time
 * 
 */
public class Configuration {
	private String ipAddress;
	private String userName;
	private String userPassword;
	private String virtualHost;
	private String exchange;

	/**
	 * @return the ipAddress - the IP Address of the rabbitmq server
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress - the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName - the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the userPassword
	 */
	public String getUserPassword() {
		return userPassword;
	}

	/**
	 * @param userPassword - the userPassword to set
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	/**
	 * @return the virtualHost
	 */
	public String getVirtualHost() {
		return virtualHost;
	}

	/**
	 * @param virtualHost - the virtualHost to set
	 */
	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}

	/**
	 * @return the exchange
	 */
	public String getExchange() {
		return exchange;
	}

	/**
	 * @param exchange - the exchange to set
	 */
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	/**
	 * @param args - command line arguments
	 */
	public void initialize(String [] args) {
		String current ="";
		try{

			// get default values from the user.ini file located in the resources file
			current = new java.io.File( "." ).getCanonicalPath();
			Properties p = new Properties();
			p.load(new FileInputStream(current+"/resources/user.ini"));
			ipAddress = p.getProperty("ipAddress");
			userName = p.getProperty("userName");
			userPassword = p.getProperty("userPassword");
			virtualHost = p.getProperty("virtualHost");
			exchange = p.getProperty("exchange");
		}
		catch (Exception e) {
			System.out.println(e);
		}

		// process command line args if any to overrule defaults
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
			case "-e":
				exchange = cmd.valueOf("-e");
				i++;
				break;
			default:
				System.out.println("Unknown argument: " + args[i] +"\n");
				System.out.println("Usage:");
				System.out.println("\tDevDasMain [-i address] [-u username] [-p password] [-v virtualhost] [-e exchange]");
				System.exit(0);
				break;		

			}

		}

	}

	/**
	 * 
	 * @param args
	 */
	public Configuration(String[] args) {
		super();
		this.initialize(args);


	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Configuration main = new Configuration(args);

	}

}
