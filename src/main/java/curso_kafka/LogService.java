package curso_kafka;

import java.time.Duration;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class LogService {


	public static void main(String[] args) {
		var consumer = new KafkaConsumer<String, String>(produceProperties());
		
		consumer.subscribe(Pattern.compile("ECOMMERCE.*"));
		
		while(true) 
		{
			var records = consumer.poll(Duration.ofMillis(100));
			
			if(!records.isEmpty()) {				
				for(var record : records) {
					System.out.println("--------------------------------------------");
					System.out.println("Logging message from " + record.topic());
					System.out.println("-> " + record.key());
					System.out.println("-> " + record.value());
					System.out.println("-> " + record.partition());
					System.out.println("-> " + record.offset());
				}
			}
		}	
	}

	private static Properties produceProperties() {
		var properties = new Properties();
		
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");		
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, LogService.class.getSimpleName());
				
		return properties;
	}
}
