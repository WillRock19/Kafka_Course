package curso_kafka_ecommerce;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import curso_kafka.consumer.KafkaService;
import curso_kafka.dispatcher.Message;

public class LogService 
{
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException 
	{
		var logService = new LogService();
		
		try(var service = new KafkaService<String>(
				LogService.class.getSimpleName(), 
				Pattern.compile("ECOMMERCE.*"), 
				logService::parseRecord,
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
