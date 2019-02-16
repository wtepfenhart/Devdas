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
    
	public AgentMessage(AgentMessage command)
    {
    	id = (UUID.randomUUID()).toString();
	    source = hostID;
	    destination = command.source;
	    topic = command.topic;
	    interest = command.interest;
	    
	    ArrayList<String> p = new ArrayList<String>();
	    p.add(command.id);
		parms.put("ReplyTo", p);
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
		source = (String) jo.getOrDefault("source", source);
		destination = (String) jo.getOrDefault("destination", destination);
		topic = (String) jo.getOrDefault("topic", topic);
		interest = (String) jo.getOrDefault("interest", interest);
		parms = (Map<String, ArrayList<String>>) jo.getOrDefault("parms", parms);
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

	public boolean addParam(boolean replace, String key, String... value)
    {
		boolean result = false;
    	if(key != null) //Should never add null keys
    	{
    		for (String v: value)
    		{
    			if (parms.containsKey(key))
    			{
    				if (replace)
    				{
    					parms.get(key).clear();
    					result = parms.get(key).add((v == null) ? "" : v);
    				}
    				else
    					result = parms.get(key).add((v == null) ? "" : v);
    			}
    			else
    			{
    				ArrayList<String> vp = new ArrayList<String>();
    				vp.add((v == null) ? "" : v);
    				parms.put(key, vp);

    				result = parms.containsKey(key);
    			}
    		}
    	}
    	
    	return result;
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
    	try
    	{
    		return this.parms.get(key).get(index);
    	}
    	catch(NullPointerException e)
    	{
    		return null;
    	}
    	catch(IndexOutOfBoundsException e)
    	{
    		return null;
    	}
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

	public String getDestination()
	{
		return destination == null ? "" : destination;
	}

	public void setDestination(String destination)
	{
		if(destination == null)
		{
			this.destination = "";
		}
		else
		{
			this.destination = destination;
		}
	}

	public void setTopic(String string)
	{
		if(string == null)
		{
			this.topic = "";
		}
		else
		{
			this.topic = string;
		}
	}
	
	public void setInterest(String interest)
	{
		if(interest == null)
		{
			this.interest = "";
		}
		else
		{
			this.interest = interest;
		}
	}
	
	public String getInterest()
	{
		return interest == null ? "" : interest;
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
		return topic == null ? "" : topic;
	}
	
	public String getSource()
	{
		return source == null ? "" : source;
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
		try
		{
			return parms.values().toArray(new String[parms.values().size()]);
		}
		catch(NullPointerException e)
		{
			return null;
		}
	}
}