package curso_kafka;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;


/*
 * Log will be a rare scenario where we can receive messages of any type and want to deserialize it.
 * The problem is: if we just use String.class as the type, it will throw an error when it receives 
 * a different type.
 * 
 *  To deal with this, we need a way to inform which are the "different" properties that our log may
 *  need to deserialize. We can do this passing a builder, or just informing in some other way. Here
 *  we are going to inform it passing a Map to the KafkaService's constructor, and that Map will 
 *  contain extra properties that we want to be added in the properties that will be passed on creating
 *  our Kafka's consumer. 
 */
public class LogService 
{
	public static void main(String[] args) 
	{
		var logService = new LogService();
		
		try(var service = new KafkaService(
				EmailService.class.getSimpleName(), 
				Pattern.compile("ECOMMERCE.*"), 
				logService::parseRecord,
				String.class,
				Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())))
		{
			service.run();
		}	
	}

	private void parseRecord(ConsumerRecord<String,String> record) 
	{
		System.out.println("--------------------------------------------");
		System.out.println("Logging message from " + record.topic());
		System.out.println("-> " + record.key());
		System.out.println("-> " + record.value());
		System.out.println("-> " + record.partition());
		System.out.println("-> " + record.offset());
	}
}
