package curso_kafka_ecommerce;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

public class LogService 
{
	public static void main(String[] args) 
	{
		var logService = new LogService();
		
		try(var service = new KafkaService<>(
				LogService.class.getSimpleName(), 
				Pattern.compile("ECOMMERCE.*"), 
				logService::parseRecord,
				String.class,
				Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())))
		{
			service.run();
		}	
	}

	private void parseRecord(ConsumerRecord<String, Message<String>> record) 
	{
		System.out.println("--------------------------------------------");
		System.out.println("Logging message from " + record.topic());
		System.out.println("-> " + record.key());
		System.out.println("-> " + record.value());
		System.out.println("-> " + record.partition());
		System.out.println("-> " + record.offset());
	}
}
