package curso_kafka.consumer.interfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.dispatcher.Message;

public interface IConsumerService<T> 
{
	String getTopic();
	String getConsumerGroup();
	
	/* You could use a ConsumerException instead of this all, so you could make the person who implements this
	 * the one responsible to throws the exception or threat it IF THEY CHOOSE TO.
	 */
	void parseRecord(ConsumerRecord<String, Message<T>> record) throws IOException, InterruptedException, ExecutionException, SQLException;
}
