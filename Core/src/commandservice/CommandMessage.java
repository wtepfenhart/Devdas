package commandservice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;

/**
 * Basic message format for system-level messages detailing control commands,
 * such as restart, stop, report, etc, which is used to get individual agents to
 * fulfill system level needs. Can write to a JSON string and read values from a
 * JSON string which is passed through RabbitMQ.
 * 
 * Might want to look into using the command pattern for implementing the
 * actions of the commands as appropriate for the individual agent.
 * 
 * @author B. T. Johnson
 */
public class CommandMessage
{
        //Class-level variable
	public static String hostID;
	
    private String id;
    private String type;
    private String source;
    private String destination;
    
    private Map<String, String> parms = new HashMap<String, String>();
    
    /**
     * Default constructor
     */
    public CommandMessage()
    {
    	id = (UUID.randomUUID()).toString();
    	type = "";
    	source = hostID;
    	setDestination("");
    }
    
    /**
     * @parms JSONObject JSON object to be read
     */
    public CommandMessage(JSONObject j)
    { 	
    	this.read(j);
    }
    
    
    public CommandMessage(CommandMessage command)
    {
    	id = (UUID.randomUUID()).toString();
	    type = "Response";
	    source = hostID;
	    setDestination(command.source);
		parms.put("ReplyTo", command.id);
    }
    
    /**
     * Reads a JSONObject for each expected key-value mapping. If the value is
     * null or an empty string (""), the value of the parameter is set to null.
     * 
     * @parms j JSONObject to be read
     */
	public void read(JSONObject jo)
    {
		id = (String) jo.get("messageID");
		type = (String) jo.get("type");
		source = (String) jo.get("source");
		setDestination((String) jo.get("destination"));
		parms = (Map<String, String>) jo.get("parms");
    }
    
	
    
    @Override
    public String toString()
    {
        return this.toJSONString();
    }
    
    /**
     * Returns a JSON string representation of this object. The string
     * representation consists of a list of key-value mappings enclosed in
     * braces ("{}"). Adjacent mappings are separated by the characters ", "
     * (comma and space). Each key-value mapping is rendered as the key followed
     * by a colon (":") followed by the associated value.
     * 
     * @return JSON string
     */
    @SuppressWarnings("unchecked")
	public String toJSONString()
    {
        JSONObject j = new JSONObject();
        j.put("id", id);
        j.put("type", type);
        j.put("source", source);
        j.put("destination", getDestination());
        j.put("parms", parms);
             
        return j.toJSONString();
    }
    
    public String getType() {
    	return type;
    }
    
    public boolean isResponse()
    {
        return type.equals("Response");
    }
    
    public boolean isCommand()
    {
        return type.equals("Command");
    }
    
    public boolean isBroadcast()
    {
    	return type.equals("Broadcast");
    }
    
    public boolean isNone()
    {
    	return !(isResponse() && isCommand() && isBroadcast());
    }
    
////////////////////////////*SETTERS*////////////////////////////
    public void addParam(String key, String value)
    {		
    	parms.put(key, (value == null) ? "" : value);    	
    }
    
////////////////////////////*GETTERS*////////////////////////////
    public String getParam(String key)
    {
    	return this.parms.get(key);
    }
    
    /**
     * @parms args Command line arguments
     * Used for debugging and testing the class code
     */
    public static void main(String[] args)
    {
/*        JSONObject o = new JSONObject();
        o.put("Source", "Agent 1");
        o.put("Destination", "Agent 2");
        System.out.println(o);
        
        CommandMessage commander1 = new CommandMessage();
        commander1.read(o.toJSONString());
        
        o.put("Command", "Start");
        o.put("Response", "");
        o.put("Explanation", "Why?");
        System.out.println(o);
        CommandMessage commander2 = new CommandMessage();
        commander2.read(o);
        
        System.out.println("Com1: " + commander1);
        System.out.println("Com2: " + commander2);
        
        commander1.addParam("Command", "Start");
        
        System.out.println("After ---> \tCom1: " + commander1);
        
        if(commander1.isCommand())
        {
            System.out.println(commander1.getParam("Command"));
        }
        else if (commander1.isResponse())
        {
            System.out.println(commander1.getParam("Response"));
        }
        else
        {
            System.out.println("No command nor response in cmd " + commander1.getParam("CommandID"));
        } */
    }

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
}