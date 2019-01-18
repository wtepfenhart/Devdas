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
	public static String hostID; //What is this set to by default?
	
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
	@SuppressWarnings("unchecked")
	public void read(JSONObject jo)
    {
		id = (String) jo.getOrDefault("messageID", id);
		type = (String) jo.getOrDefault("type", type);
		source = (String) jo.getOrDefault("source", source);
		setDestination((String) jo.getOrDefault("destination", destination));
		parms = (Map<String, String>) jo.getOrDefault("parms", parms);
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
    
////////////////////////////*SETTERS*////////////////////////////
    public boolean addParam(String key, String value)
    {
    	if (key != null)
    	{
    		parms.put(key, (value == null) ? "" : value);
    		return parms.containsKey(key);
    	}
    	
    	return false;
    }

	public void setDestination(String destination)
	{
		this.destination = destination;
	}
    
////////////////////////////*GETTERS*////////////////////////////
    public String getParam(String key)
    {
    	try
    	{
    		return this.parms.get(key);
    	}
    	catch(NullPointerException e)
    	{
    		return null;
    	}
    }
    
    public String getDestination()
	{
		return destination == null ? "" : destination;
	}
    
    public String getType()
    {
    	return type == null ? "" : type;
    }
    
    /**
     * @parms args Command line arguments
     * Used for debugging and testing the class code
     */
    public static void main(String[] args)
    {   
    	JSONObject o = new JSONObject();
        o.put("type", "Test");
        	System.out.println(o);
        
        CommandMessage commander1 = new CommandMessage();
        	System.out.println(" [.] Before read: " + commander1);
        	
        commander1.addParam("Command", "Start");
        commander1.addParam("Response", "");
        commander1.addParam("Explanation", "Why?");
            System.out.println(" [.] After added params: " + commander1);
            
        commander1.addParam("Command", "Stop");
        	System.out.println(" [.] After command change: " + commander1);
       
        commander1.read(o);
        	System.out.println(" [.] After read: " + commander1);
        	
        CommandMessage commander2 = new CommandMessage(commander1);
        	System.out.println(" [.] New commander: " + commander2);
        commander2 = new CommandMessage(o);
        
        if(commander1.getType().equals("Test"))
        {
            System.out.println(" [.] Explanation: " + commander1.getParam("Explanation"));
        }
        else
        {
        	System.err.println("ERROR");
        }
    }
}