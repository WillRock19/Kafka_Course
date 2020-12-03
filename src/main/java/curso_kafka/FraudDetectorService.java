package curso_kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class FraudDetectorService {

	public static void main(String[] args) {
		var consumer = new KafkaConsumer<String, String>(produceProperties());
		
		//Pass a list of topics that the consumer will be subscribed. Usually, each service will be subscribed to a 
		//single topic, since it usually makes a single, specific, objective inside of it
		consumer.subscribe(Collections.singletonList("ECOMMERCE_NEW_ORDER"));
		
		//Gonna be listening for any messages sended to kafka FOREVER
		while(true) 
		{
			//Defining the amount of time the subscriber will be looking for a data inside Kafka before proceed code execution
			var records = consumer.poll(Duration.ofMillis(100));
			
			if(!records.isEmpty()) {
				System.out.println("Founded " + records.count() + " registers!");
				
				for(var record : records) {
					System.out.println("--------------------------------------------");
					System.out.println("Processing new order. Checking for frauds...");
					System.out.println("-> " + record.key());
					System.out.println("-> " + record.value());
					System.out.println("-> " + record.partition());
					System.out.println("-> " + record.offset());
					
					try{
						Thread.sleep(5000);	
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("Order processed! :)");
				}
			}
		}	
	}

	
	private static Properties produceProperties() {
		var properties = new Properties();
		
		//Defining in what IP address the Kafka is running
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		
		//Defining how the consumer will deserialize the data that Kafka received from the producers
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		
		//A consumer demands a group, so Kafka can understand "wich group is dealing with the messages."
		//If I have a service inside a group, it will receive all messages
		//If there are two services of same group, the messages will be divided to be processed by both services
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, FraudDetectorService.class.getSimpleName());
				
		return properties;
	}

}
