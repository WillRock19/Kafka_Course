package curso_kafka.consumer.interfaces;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.dispatcher.Message;

public interface IConsumerService<T> 
{
	String getTopic();
	String getConsumerGroup();
	void parseRecord(ConsumerRecord<String, Message<T>> record) throws IOException, InterruptedException, ExecutionException;
}
