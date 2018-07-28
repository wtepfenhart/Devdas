package commandservice;

import java.util.Scanner;
import devdas.Configuration;

/**
 * Simulates the behavior of the CommandService classes by allowing the user to issue commands
 * through the publisher and receiving them synchronously through the receiver.
 * 
 * @author B-T-Johnson
 */
public class GenericProg
{
	public static void main(String[] args)
	{
		//Initialize pub/sub
		Configuration config = new Configuration(args);
		String exchange = "Testing";
		CommandServicePublisher pub = new CommandServicePublisher(config, exchange);
			@SuppressWarnings("unused")
		CommandServiceSubscriber sub = new CommandServiceSubscriber(config, exchange);
		
		pub.start();
		
		String command = "";
		String response = "";
		
		System.out.println();
		
		//Initialize scanner
		Scanner keyboard = new Scanner(System.in);
		
		//Test
		while(!command.equals("-1"))
		{	
			//Initialize commander
			CommandService commander = new CommandService();
				commander.setSource("Keyboard");
				commander.setDestination(exchange);
				System.out.println("\t" + commander.toJSONString());
				
			System.out.println("--------------");
			
			//Ask for command
			System.out.print("Send a command: ");
				command = keyboard.nextLine();
				System.out.println();
			
			//Set command and send
			commander.setCommand(command);
				System.out.println("\t" + commander.toJSONString());
			pub.setMessage(commander);
			
			System.out.println("--------------");
			
			//Ask for response
			System.out.print("Send a response: ");
				response = keyboard.nextLine();
				System.out.println();
			
			//Set response and send
			commander.setResponse(response);
				System.out.println("\t" + commander.toJSONString());
			pub.setMessage(commander);
			
			System.out.println("==============");
		}
		
		keyboard.close();
	}
}