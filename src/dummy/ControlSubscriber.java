package dummy;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

import devdas.Configuration;
import devdas.ExchangePublisher;
import devdas.ExchangeSubscriber;

public class ControlSubscriber extends ExchangeSubscriber{
	DevdasControl program;
	

	public ControlSubscriber(Configuration config, String exch, DevdasControl prog) {
		super(config,exch);
		program = prog;
		// TODO Auto-generated constructor stub
	}
	
	public void handleMessage(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,String message) {
		program.processCommand(consumerTag, envelope, properties, message);
		//System.out.println(" [x] Received '" + consumerTag + " Messsage: " + message + "'");
	}

}
