package curso_kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailService {

	public static void main(String[] args) {
		var emailService = new EmailService();
		
		try(var service = new KafkaService(
				EmailService.class.getSimpleName(), 
				"ECOMMERCE_SEND_EMAIL", 
				emailService::parseRecord,
				String.class,
				Map.of())) /*We will pass an empty map of properties, since we don't want to add nothing more (we could use, for java, a new HashMap<>() either)*/
		{
			service.run();
		}
	}

	private void parseRecord(ConsumerRecord<String,String> record) 
	{
		System.out.println("--------------------------------------------");
		System.out.println("Sending email...");
		System.out.println("-> " + record.key());
		System.out.println("-> " + record.value());
		System.out.println("-> " + record.partition());
		System.out.println("-> " + record.offset());
		
		try{
			Thread.sleep(1000);	
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("E-mail sended ^^");
	}
}
