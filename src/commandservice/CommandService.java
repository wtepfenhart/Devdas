package commandservice;

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
public class CommandService
{
        //Class-level variable
    private static int ID = 0;
    
    /**
     * Criteria
     * 
     * @param cmdID Command identifier
     * @param src Source of command
     * @param dest Destination of command
     * @param cmd Command
     * @param resp Response
     * @param exp Explanation (if error)
     */
    private String cmdID, src, dest, cmd, resp, exp;
    
    /**
     * Default constructor
     */
    public CommandService()
    {}
    
    /**
     * @param jsonStr JSON string to be read
     */
    public CommandService(JSONObject j)
    {   
    	this.read(j);
    }
    
    /**
     * @param jsonStr JSON string to be read
     */
    public CommandService(String jsonStr)
    {   
    	this.read(jsonStr);
    }
    
    /**
     * Reads a JSONObject for each expected key-value mapping. If the value is
     * null or an empty string (""), the value of the parameter is set to null.
     * 
     * @param j JSONObject to be read
     */
    public void read(JSONObject j)
    {	
    	this.setCommandID((String) j.get("CID"));
    	this.setSource((String) j.get("Source"));
        this.setDestination((String) j.get("Destination"));
        this.cmd = (j.get("Command") == null || ((String) j.get("Command")).equals("")) ? null : (String) j.get("Command");
        this.setResponse((String) j.get("Response"));
        this.setExplanation((String) j.get("Explanation"));
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
			o = parser.parse(jsonStr); //Captures key-value pair in Object o
			
			JSONObject j = (JSONObject) o;
			
			this.read(j);
		}
		catch (ParseException e)
		{
			System.err.println("Unknown message format: " + jsonStr +"\n");
			System.err.println("Usage:");
			System.err.println("\t{\"CID\": [commandID], \"Source\": [source], \"Destination\": [destination], \"Command\": [command], \"Response\": [response], \"Explanation\": [explanation]}");
		}
    }
    
    /**
     * Reads a string which is set to a specific key for each expected key-value mapping. If the value is
     * null or an empty string (""), the value of the parameter is set to null.
     * 
     * @param jsonStr JSON string to be read
     * @deprecated Does not set the class-level variable cmdID upon method call. Use {@link #read(JSONObject)} or {@link #read(String)} instead.
     */
    @Deprecated
    public void read(String key, String value)
    {
    	switch (key.toLowerCase())
    	{
            case "source":
                this.setSource(value);
                break;
            case "destination":
                this.setDestination(value);
                break;
            case "command":
            	this.cmd = (value == null || value.equals("")) ? null : value;
                break;
            case "response":
                this.setResponse(value);
                break;
            case "explanation":
                this.setExplanation(value);
                break;
            default:
                break;
        }
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
        
        j.put("CID", cmdID);
        j.put("Source", src);
        j.put("Destination", dest);
        j.put("Command", cmd);
        j.put("Response", resp);
        j.put("Explanation", exp);
        
        return j.toJSONString();
    }
    
    public boolean hasResponse()
    {
        return !(resp == null || resp.equals(""));
    }
    
    public boolean hasCommand()
    {
        return !(cmd == null || cmd.equals(""));
    }
    
////////////////////////////*SETTERS*////////////////////////////
    private void setCommandID(String cmdID) //Used by .read()
    {
		this.cmdID = (cmdID == null || cmdID.equals("")) ? null : cmdID;
	}
    
    public void setSource(String src)
    {
    	this.src = (src == null || src.equals("")) ? null : src;
    }
    
    public void setDestination(String dest)
    {
    	this.dest = (dest == null || dest.equals("")) ? null : dest;
    }
    
    public void setResponse(String resp)
    {
        this.resp = (resp == null || resp.equals("")) ? null : resp;
    }
    
    public void setExplanation(String exp)
    {
        this.exp = (exp == null || exp.equals("")) ? null : exp;
    }
    
    public void setCommand(String cmd)
    {	
    	this.cmdID = String.valueOf(ID++);
    	
        this.cmd = (cmd == null || cmd.equals("")) ? null : cmd;
    }
    
////////////////////////////*GETTERS*////////////////////////////
    public String getResponse()
    {
        if (resp != null)
            return resp;
        return "Nil";
    }
    
    public String getCommandID()
    {
    	if (cmdID != null)
            return cmdID;
        return "Nil";
    }
    
    public String getDestination()
    {
        if (dest != null)
            return dest;
        return "Nil";
    }
    
    public String getSource()
    {
        if (src != null)
            return src;
        return "Nil";
    }
    
    public String getCommand()
    {
        if (cmd != null)
            return cmd;
        return "Nil";
    }
    
    public String getExplanation()
    {
        if (exp != null)
            return exp;
        return "Nil";
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
        
        CommandService commander1 = new CommandService();
        commander1.read(o.toJSONString());
        
        o.put("Command", "Start");
        o.put("Response", "");
        o.put("Explanation", "Why?");
        System.out.println(o);
        CommandService commander2 = new CommandService();
        commander2.read(o);
        
        System.out.println("Com1: " + commander1);
        System.out.println("Com2: " + commander2);
        
        commander1.setCommand("Start");
        
        System.out.println("After ---> \tCom1: " + commander1);
        
        if(commander1.hasCommand())
        {
            System.out.println(commander1.getCommand());
        }
        else if (commander1.hasResponse())
        {
            System.out.println(commander1.getResponse());
        }
        else
        {
            System.out.println("No command nor response in cmd " + commander1.getCommandID());
        }
    }
}