package speaker;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

public interface DevdasControl {
	public void processCommand(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,String message);
}
