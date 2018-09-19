package commandservice;

import devdas.Configuration;
import devdas.ExchangePublisher;

import java.util.Scanner;

import org.json.simple.JSONObject;

/**
 * @author B-T-Johnson
 */
public class CommandServicePublisher extends ExchangePublisher
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
    	CommandServiceMessage cmd = new CommandServiceMessage();
    	cmd.read(msg);
    	
    	super.setMessage(cmd.toJSONString());
    }
    
    /**
     * @param cmd the CommandService object to set for sending
     */
    public void setMessage(CommandServiceMessage cmd)
    {
    	super.setMessage(cmd.toJSONString());
    }
    
    /**
     * @param jsonMsg the message to set for sending
     */
    @Override
    public void setMessage(String jsonMsg)
    {
    	CommandServiceMessage cmd = new CommandServiceMessage();
    	cmd.read(jsonMsg);
    	
        super.setMessage(cmd.toJSONString());
    }
    
    /**
     * @param args command line arguments
     * 
     * Used for debugging and testing the send class code
     */
    // TODO replace with junit testing
    @SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException
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
        cmdSender.setMessage(speak.toJSONString());
        	speak.put("Explanation", "Second message to send");
        cmdSender.setMessage(speak.toJSONString());
        
        //Allow user interaction
        Scanner scanner = new Scanner(System.in);
		CommandServiceMessage command = new CommandServiceMessage();
			command.setCommand(scanner.nextLine());
			cmdSender.setMessage(command);
			
			command.setCommand(scanner.nextLine());
			cmdSender.setMessage(command);
        
        //Terminate
        sleep(10);
        cmdSender.setRunning(false);
        scanner.close();
    }
}