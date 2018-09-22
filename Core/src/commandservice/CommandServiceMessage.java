package commandservice;

import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
public class CommandServiceMessage
{
        //Class-level variable
    private static int ID = 0;
    
    /**
     * Criteria
     * 
     * @param CommandID Command identifier
     * @param Source Source of message
     * @param Destination Destination of message
     * @param cmd Command
     * @param resp Response
     * @param exp Explanation (if error)
     */
    private Map<String, String> param = new HashMap<String, String>();
    private String t;
    
    /**
     * Default constructor
     */
    public CommandServiceMessage()
    {
    	this.t = "Command";
    	
    	param.put("CommandID", String.valueOf(ID++));
    	param.put("Source", "");
    	param.put("Destination", "");
    	param.put("Command", "");
    	param.put("Response", "");
    	param.put("Explanation", "");
    }
    
    /**
     * @param JSONObject JSON object to be read
     */
    public CommandServiceMessage(JSONObject j)
    {
    	param.put("CommandID", "");
    	param.put("Source", "");
    	param.put("Destination", "");
    	param.put("Command", "");
    	param.put("Response", "");
    	param.put("Explanation", "");
    	
    	this.read(j);
    }
    
    public CommandServiceMessage(String jsonStr)
    {
    	param.put("CommandID", "");
    	param.put("Source", "");
    	param.put("Destination", "");
    	param.put("Command", "");
    	param.put("Response", "");
    	param.put("Explanation", "");
    	
    	this.read(jsonStr);
    }
    
    public CommandServiceMessage(CommandServiceMessage command)
    {
	    param.put("CommandID", "");
		param.put("Source", "");
		param.put("Destination", "");
		param.put("Command", "");
		param.put("Response", "");
		param.put("Explanation", "");
    	
    	this.read(command);
    }
    
    /**
     * Reads a JSONObject for each expected key-value mapping. If the value is
     * null or an empty string (""), the value of the parameter is set to null.
     * 
     * @param j JSONObject to be read
     */
	public void read(JSONObject jo)
    {
    	this.t = jo.get("Type") == null ? "Command" : (String) jo.get("Type");
    	Object[] key = jo.keySet().toArray();
    	Object[] value = jo.values().toArray();
    	
    	param.put((String) key[0], (String) value[0]); //Type
    	for (int i = 0; i < ((Map) value[1]).keySet().toArray().length; i++)
    	{
    		param.put((String) key[1], (String) ((Map) value[1]).values().toArray()[i]); //TODO CRITICAL ERROR; value[i] is not a String, but a Map
    	}
    }
    
	/**
     * Reads a JSON string for each expected key-value mapping. If the value is
     * null or an empty string (""), the value of the parameter is set to null.
     * 
     * @param jsonStr JSON string to be read
     */
    public void read(String jsonStr)
    {	
    	JSONParser parser = new JSONParser();
		Object o = new JSONParser();
		
		try
		{
			//Capture key-value pair in Object o
			o = (JSONObject) parser.parse(jsonStr);
			
			JSONObject j = (JSONObject) o;
			
			this.read(j);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			System.err.println("Unknown message format: " + jsonStr +"\n");
			System.err.println("Usage:");
			System.err.println("\t{\"CID\": [commandID], \"Source\": [source], \"Destination\": [destination], \"Command\": [command], \"Response\": [response], \"Explanation\": [explanation]}");
		}
    }
    
    public void read(CommandServiceMessage command)
    {
    	read(command.toJSONString());
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
        
        j.put("Type", t);
        j.put("Parameters", param);
        
        return j.toJSONString();
    }
    
    public boolean isResponse()
    {
        return t.equals("Response");
    }
    
    public boolean isCommand()
    {
        return t.equals("Command");
    }
    
    public boolean isBroadcast()
    {
    	return t.equals("Broadcast");
    }
    
    public boolean isNone()
    {
    	return !(isResponse() && isCommand() && isBroadcast());
    }
    
////////////////////////////*SETTERS*////////////////////////////
    public void addParam(String key, String value)
    {		
    	switch(key)
    	{
    		case "Source":
    			this.param.put(key, (value == null) ? "" : value);
    			break;
    		case "Destination":
    			this.param.put(key, (value == null) ? "" : value);
    			break;
    		case "Command":    			
    			if (value != null)
    	    	{
    	    		param.put("CommandID", String.valueOf(ID++));
    	    	}
    			this.param.put(key, (value == null) ? "" : value);
    			break;
    		case "Response":
    			this.param.put(key, (value == null) ? "" : value);
    			break;
    		case "Explanation":
    			this.param.put(key, (value == null) ? "" : value);
    			break;
    		default:
    			this.param.put(key, (value == null) ? "" : value);
    	}
    }
    
////////////////////////////*GETTERS*////////////////////////////
    public String getParam(String key)
    {
    	return this.param.get(key);
    }
    
    /**
     * @param args Command line arguments
     * Used for debugging and testing the class code
     */
    // TODO replace with junit testing
    @SuppressWarnings("unchecked")
	public static void main(String[] args)
    {
        JSONObject o = new JSONObject();
        o.put("Source", "Agent 1");
        o.put("Destination", "Agent 2");
        System.out.println(o);
        
        CommandServiceMessage commander1 = new CommandServiceMessage();
        commander1.read(o.toJSONString());
        
        o.put("Command", "Start");
        o.put("Response", "");
        o.put("Explanation", "Why?");
        System.out.println(o);
        CommandServiceMessage commander2 = new CommandServiceMessage();
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
        }
    }
}