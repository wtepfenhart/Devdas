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
		CommandServicePublisher pub;
		CommandServiceSubscriber sub;
		
		String command;
		
		//Initialize scanner
			@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);
		
		//Ask user for exchange
		System.out.print("Name the exchange to use: ");
			String exchange = keyboard.nextLine();
			System.out.println();
		
		//Set exchange and configuration
		pub = new CommandServicePublisher(config, exchange); 
		sub = new CommandServiceSubscriber(config, exchange);
		
		pub.start();
		
		try
		{
			Thread.sleep(1000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println();
		
		//Test
		while(true)
		{
			//Initialize commander
			CommandService commander = new CommandService();
				commander.setSource("Keyboard");
				commander.setDestination(exchange);
				
				System.out.println("\tCreated new commander: " + commander.toJSONString());
				
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
			
			//Receive response
			System.out.println("Response:");
			
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("--------------");
			
			System.out.println("Set response");
				pub.setMessage(sub.getCommand());
			
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("==============");
		}
	}
}