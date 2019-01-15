/**
 * 
 * Author: wtepfenhart
 * File: AgentMessage.java
 * Date: Sep 25, 2018
 * Copyright (C) 2018
 *
 */
package commandservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author wtepfenhart
 *
 */
public class AgentMessage {

	public static String hostID; //What is this set to by default?
	
    private String id;
    private String source;
    private String destination; 
    private String topic;
    private String interest;
    private Map<String, ArrayList<String>> parms = new HashMap<String, ArrayList<String>>();
    
    /**
     * Default constructor
     */
    public AgentMessage()
    {
    	id = (UUID.randomUUID()).toString();
    	source = hostID;
    	setDestination("");
    	
    }
    
    /**
     * @parms JSONObject JSON object to be read
     */
    public AgentMessage(JSONObject j)
    { 	
    	this.read(j);
    }
    
	@SuppressWarnings("serial")
	public AgentMessage(AgentMessage command)
    {
    	id = (UUID.randomUUID()).toString();
	    source = hostID;
	    destination = command.source;
	    topic = command.topic;
	    interest = command.interest;
		parms.put("ReplyTo", new ArrayList<String>(){{add(command.id);}});
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
		id = (String) jo.get("messageID");
		source = (String) jo.get("source");
		destination = (String) jo.get("destination");
		topic = (String) jo.get("topic");
		interest = (String) jo.get("interest");
		parms = (Map<String, ArrayList<String>>) jo.get("parms");
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
        j.put("messageID", id);
        j.put("source", source);
        j.put("topic", topic);
        j.put("interest", interest);
        j.put("destination", getDestination());
        j.put("parms", parms);
             
        return j.toJSONString();
    }

    @SuppressWarnings("serial")
	public void addParam(boolean replace, String key, String... value)
    {
    	for (String v: value)
		{
	    	if (parms.containsKey(key))
	    	{
	    		if (replace)
	    		{
	    			parms.get(key).clear();
	    			parms.get(key).add(v);
	    		}
	    		else
	    			parms.get(key).add(v);
	    	}
	    	else
	    	{
	    		parms.put(key, new ArrayList<String>(){{add(v);}});
	    	}
		}
    }
    
    //Assumes no replacement
    public void addParam(String key, String... value)
    {
    	this.addParam(false, key, value);
    }
    
    public String[] getParamList(String key)
    {
    	try
    	{
    		return this.parms.get(key).toArray(new String[parms.get(key).size()]);
    	}
    	catch(NullPointerException e)
    	{
    		return null;
    	}
    }
    
    public String getParam(String key, int index)
    {
    	return this.parms.get(key).get(index);
    }
    
    /**
     * @parms args Command line arguments
     * Used for debugging and testing the class code
     */
    public static void main(String[] args)
    {
    	AgentMessage cmd = new AgentMessage();
    	
    	cmd.addParam("SingleTest", "Wrd");
    	cmd.addParam("DoubleTest", "Wrd1","Wrd2");
    	
    	System.out.println(cmd);
    	
    	JSONParser parser = new JSONParser();
		JSONObject json;
		try
		{
			json = (JSONObject) parser.parse(cmd.toJSONString());
			AgentMessage msg = new AgentMessage(json);
			
			System.out.println("PARSED: " + msg);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		cmd.addParam(false, "DoubleTest", "Wrd3");
		
		System.out.println(cmd);
		
		cmd.addParam(true, "DoubleTest", "replacement");
		
		System.out.println(cmd);
    }

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public void setTopic(String string) {
		topic = string;
	}
	
	public void setInterest(String interest)
	{
		this.interest = interest;
	}
	
	public String getInterest()
	{
		return interest;
	}

    public String getRoute() {
    	if (destination != "") {
    		return destination;
    	} else if (topic != "") {
    		return topic;
    	}
    	return "";
    }

	public String getTopic()
	{
		return topic;
	}
	
	public String getSource()
	{
		return source;
	}
	
	public String getID()
	{
		return this.id;
	}
	
	/**
	 *Returns an array of all known values in the parameter mapping of an AgentMessage
	 *
	 * @return An array containing all values known by this AgentMessage
	 */
	public String[] getAllParams()
	{
		return parms.values().toArray(new String[parms.values().size()]);
	}
}