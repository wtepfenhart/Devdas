package commandservice;

import devdas.Configuration;
import devdas.RoutingPublisher;

import java.util.Scanner;
import org.json.simple.JSONObject;

/**
 * @author B-T-Johnson
 */
public class AgentServicePublisher extends RoutingPublisher
{   
    public AgentServicePublisher(Configuration config, String exch)
    {
        super(config, exch);
    }
    
    
    
    /**
     * @param msg the JSON object to set for sending
     */
    public void setMessage(JSONObject msg)
    {
    	AgentMessage cmd = new AgentMessage();
    	cmd.read(msg);
    	
    	super.setMessage(cmd.toJSONString(),cmd.getDestination());
    }
    
    /**
     * @param cmd the AgentService object to set for sending
     */
    public void setMessage(AgentMessage cmd)
    {
    	super.setMessage(cmd.toJSONString(), cmd.getRoute());
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
        AgentServicePublisher cmdSender;
        Configuration config = new Configuration(args);
        cmdSender = new AgentServicePublisher(config, "Control");
        
        //Send first command (as JSONObject)
        cmdSender.start();
        JSONObject first = new JSONObject();
        	first.put("Agent", "First");
        cmdSender.setMessage(first);

        //Change exchange
        cmdSender = new AgentServicePublisher(config, "Testing");
        cmdSender.start();
        
        //Send second message (as JSON String)
        JSONObject speak = new JSONObject();
        	speak.put("Agent", "Speak");
        cmdSender.setMessage(speak.toJSONString(),"All");
        	speak.put("Explanation", "Second message to send");
        cmdSender.setMessage(speak.toJSONString(),"All");
        
        //Allow user interaction
        Scanner scanner = new Scanner(System.in);
		AgentMessage command = new AgentMessage();
			command.addParam("Agent", scanner.nextLine());
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