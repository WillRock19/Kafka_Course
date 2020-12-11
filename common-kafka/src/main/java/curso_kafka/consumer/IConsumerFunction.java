package curso_kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.dispatcher.Message;

public interface IConsumerFunction<T> {
	void consume(ConsumerRecord<String, Message<T>> record)  throws Exception;
}
