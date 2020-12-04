package curso_kafka;

import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class LogService 
{
	public static void main(String[] args) 
	{
		var logService = new LogService();
		
		try(var service = new KafkaService(
				EmailService.class.getSimpleName(), 
				Pattern.compile("ECOMMERCE.*"), 
				logService::parseRecord,
				String.class)) //Log's type to deserialize will be a string
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
