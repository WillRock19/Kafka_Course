package curso_kafka_ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IConsumerFunction<T> {
	void consume(ConsumerRecord<String, T> record)  throws Exception;
}
