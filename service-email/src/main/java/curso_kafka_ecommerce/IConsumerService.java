package curso_kafka_ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.dispatcher.Message;

public interface IConsumerService<T> 
{
	String getTopic();
	String getConsumerGroup();
	void parseRecord(ConsumerRecord<String, Message<T>> record);
}
