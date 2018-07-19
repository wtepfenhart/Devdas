package commandservice;

import java.util.Scanner;
import org.json.simple.JSONObject;

/**
 * @author B. T. Johnson
 * 
 * Basic message format for system-level messages detailing control commands,
 * such as restart, stop, report, etc, which is used to get individual agents to
 * fulfill system level needs. Can write to a JSON string and read values from a
 * JSON string which is passed through RabbitMQ.
 * 
 * Might want to look into using the command pattern for implementing the
 * actions of the commands as appropriate for the individual agent.
 */
public class CommandService
{
        //Class-level variable
    private static int ID = 0;
    private final String cmdID;
    
    /**
     * Criteria
     * 
     * @param src Source identifier
     * @param dest Destination identifier
     * @param cmd Command identifier
     * @param resp Response identifier
     * @param exp Explanation identifier
     */
    private String src, dest, cmd, resp, exp;
    
    /**
     * Reads a JSONObject for each expected key-value mapping. If the value is
     * null or an empty string (""), the value of the parameter is set to null.
     * 
     * @param j JSONObject to be read
     */
    public CommandService(JSONObject j)
    {
        cmdID = String.valueOf(ID);
        ID++;
        
        src = ((String) j.get("Source")).equals("") ? null : (String) j.get("Source");
        dest = ((String) j.get("Destination")).equals("") ? null : (String) j.get("Destination");
        cmd = ((String) j.get("Command")).equals("") ? null : (String) j.get("Command");
        resp = ((String) j.get("Response")).equals("") ? null : (String) j.get("Response");
        exp = ((String) j.get("Explanation")).equals("") ? null : (String) j.get("Explanation");
    }
    
    /**
     * Reads a JSON string for each expected key-value mapping. If the value is
     * null or an empty string (""), the value of the parameter is set to null.
     * 
     * @param jsonStr JSON string to be read
     */
    public CommandService(String jsonStr)
    {
        cmdID = String.valueOf(ID);
        ID++;
        
        Scanner scan = new Scanner(jsonStr);
        scan.useDelimiter("(?:(?<!\")\\p{Punct}+)|(?:\\p{Punct}+(?!\"))"); //Targets any punctuation between the double-quotation character, including the character itself
        
        while (scan.hasNext())
        {
            String key = scan.next(); //Key retrieved from jsonStr, .next() will yield the value
            String value = scan.next();
            
            switch (key)
            {
                case "Source":
                    src = (value.equals("")) ? null : value;
                    break;
                case "Destination":
                    dest = (value.equals("")) ? null : value;
                    break;
                case "Command":
                    cmd = (value.equals("")) ? null : value;
                    break;
                case "Response":
                    resp = (value.equals("")) ? null : value;
                    break;
                case "Explanation":
                    exp = (value.equals("")) ? null : value;
                    break;
                default:
                    break;
            }
        }
    }
    
    @Override
    public String toString()
    {
        return "[CID: " + cmdID + ", Source: " + src + ", Destination: " + dest + ", Command: " + cmd + ", Response: " + resp + ", Explanation: " + exp + "]";
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
    public void setResponse(String resp)
    {
        this.resp = (resp.equals("")) ? null : resp;
    }
    
    public void setExplanation(String exp)
    {
        this.exp = (exp.equals("")) ? null : exp;
    }
    
    public void setCommand(String cmd)
    {
        this.cmd = (cmd.equals("")) ? null : cmd;
    }
    
////////////////////////////*GETTERS*////////////////////////////
    public String getResponse()
    {
        if (this.hasResponse())
            return resp;
        return "Nil";
    }
    
    public String getCommandID()
    {
        return cmdID;
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
        if (this.hasCommand())
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
    public static void main(String[] args)
    {
        JSONObject o = new JSONObject();
        o.put("Source", "Agent 1");
        o.put("Destination", "Agent 2");
        System.out.println(o);
        
        CommandService commander1 = new CommandService(o.toJSONString());
        
        o.put("Command", "Start");
        o.put("Response", "");
        o.put("Explanation", "Why?");
        CommandService commander2 = new CommandService(o);
        
        System.out.println("Com1: " + commander1);
        System.out.println("Com2: " + commander2);
        
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