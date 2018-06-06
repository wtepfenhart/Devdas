import java.util.Scanner;

public class LogRecieve extends Recieve {

	public LogRecieve(Configuration application, String exch) {
		super(application, exch);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void handleMessage(String msg) {
        System.err.println(" [x] Received '" + msg + "'");
		
	}

	/**
	 * Used For testing purposes
	 * 
	 * @param args - command line arguments
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Send mySender, logSender;
		@SuppressWarnings("unused")
		Recieve myReciever, myReciever2;
		Configuration config = new Configuration(args);
		myReciever = new Recieve(config, "Testing");
		myReciever2 = new LogRecieve(config, "Log");
		mySender = new Send(config, "Testing");
		mySender.start();
		logSender = new Send(config, "Log");
		logSender.start();
		mySender.sendMessage("Hello This is going to succeed!");
		logSender.sendMessage("Second message to send");
		mySender.sendMessage("Third message to send");
		Scanner scanner = new Scanner(System.in);
		String msg = scanner.nextLine();
		mySender.sendMessage(msg);
		mySender.setRunning(false);
		logSender.setRunning(false);
		scanner.close();
		System.exit(1);
	}

}
