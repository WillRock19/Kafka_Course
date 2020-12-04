package curso_kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface IConsumerFunction {
	void consume(ConsumerRecord<String, String> record);
}
