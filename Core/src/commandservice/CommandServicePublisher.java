package commandservice;

import devdas.Configuration;
import devdas.RoutingPublisher;

import java.util.Scanner;
import org.json.simple.JSONObject;

/**
 * @author B-T-Johnson
 */
public class CommandServicePublisher extends RoutingPublisher
{   
    public CommandServicePublisher(Configuration config, String exch)
    {
        super(config, exch);
    }
    
    
    
    /**
     * @param msg the JSON object to set for sending
     */
    public void setMessage(JSONObject msg)
    {
    	CommandMessage cmd = new CommandMessage();
    	cmd.read(msg);
    	
    	super.setMessage(cmd.getDestination(), cmd.toJSONString());
    }
    
    /**
     * @param cmd the CommandService object to set for sending
     */
    public void setMessage(CommandMessage cmd)
    {
    	super.setMessage(cmd.getDestination(), cmd.toJSONString());
    }
    
    
    /**
     * @param args command line arguments
     * 
     * Used for debugging and testing the send class code
     */
    // TODO replace with junit testing
    @SuppressWarnings("unchecked")
	public static void main(String[] args)
    {
        CommandServicePublisher cmdSender;
        Configuration config = new Configuration(args);
        cmdSender = new CommandServicePublisher(config, "Control");
        
        //Send first command (as JSONObject)
        cmdSender.start();
        JSONObject first = new JSONObject();
        	first.put("Command", "First");
        cmdSender.setMessage(first);

        //Change exchange
        cmdSender = new CommandServicePublisher(config, "Testing");
        cmdSender.start();
        
        //Send second message (as JSON String)
        JSONObject speak = new JSONObject();
        	speak.put("Command", "Speak");
        cmdSender.setMessage(speak.toJSONString(),"All");
        	speak.put("Explanation", "Second message to send");
        cmdSender.setMessage(speak.toJSONString(),"All");
        
        //Allow user interaction
        Scanner scanner = new Scanner(System.in);
		CommandMessage command = new CommandMessage();
			command.addParam("Command", scanner.nextLine());
			cmdSender.setMessage(command);
			
			command.addParam("Command", scanner.nextLine());
			cmdSender.setMessage(command);
        
        //Terminate
        try {
			sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        cmdSender.setRunning(false);
        scanner.close();
    }
}