package curso_kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

/*
 * In this class, we are gonna run this service twice, so it can run in paralel to deal with messages. First of all,
 * since we already created a ECOMMERCE_NEW_ORDER consumer, if we run the command below we will see that this consumer
 * has only one partition.
 * 
 *						 .\kafka-topics.bat --bootstrap-server localhost:9092 --describe
 *
 *The number of partitions is important because it will tell Kafka if it can divide the work between multiple instances
 *of a service, or if it let's it being executed by a single instance. So, to get started, we have to create more partitions
 *of this.
 *
 *To do so, let's execute the command bellow:
 *
 * 						.\kafka-topics.bat --alter --zookeeper localhost:2181 --topic ECOMMERCE_NEW_ORDER --partitions 3
 *
 * Good. Now Kafka will have created 3 partitions for our topic. if we run describe again, we'll see it.
 * 
 * Now, let's execute the FraudDetectorService in two instances. When we do this, the first service to run will get all
 * the available partitions, but when the second is executed, Kafka will redistribute the number of partitions between 
 * both. 
 * 
 * But, if we execute it, we will see only ONE of the services will receive the messages.  Why? Kafka will use a algorithm 
 * to know where to send the value, and that algorithm uses the key we are sending in NewOrderMain's ProduceRecord's constructor.
 * 
 * Since, right now, we are using the same key for all messages, It will always send the message to the same instance of a 
 * service (GOD DAMN IT, MARSHAL!!!)
 * 
 * When we want to see how the consume groups are consuming the messages, we can use the following:
 * 
 *  					.\kafka-consumer-groups.bat --all-groups --bootstrap-server localhost:9092 --describe 
 *
 * RESULT EXAMPLE:
 * 
 * GROUP           TOPIC                PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  	  LAG             							CONSUMER-ID                                                HOST            CLIENT-ID
LogService      ECOMMERCE_SEND_EMAIL 		0          56              56              0               consumer-LogService-1-418cf77d-7b2f-420e-b1b0-5f2971d57482 						/192.168.0.4    consumer-LogService-1
LogService      ECOMMERCE_NEW_ORDER  		0          32              32              0               consumer-LogService-1-418cf77d-7b2f-420e-b1b0-5f2971d57482 						/192.168.0.4    consumer-LogService-1
LogService      ECOMMERCE_NEW_ORDER  		1          13              13              0               consumer-LogService-1-418cf77d-7b2f-420e-b1b0-5f2971d57482 						/192.168.0.4    consumer-LogService-1
LogService      ECOMMERCE_NEW_ORDER  		2          18              18              0               consumer-LogService-1-418cf77d-7b2f-420e-b1b0-5f2971d57482 						/192.168.0.4    consumer-LogService-1
 * 
 * */

public class FraudDetectorService {

	public static void main(String[] args) {
		var consumer = new KafkaConsumer<String, String>(produceProperties());
		
		consumer.subscribe(Collections.singletonList("ECOMMERCE_NEW_ORDER"));
		
		while(true) 
		{
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
		
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		
		/*
		 * We could also give a specific name to the instance, since we will execute many in paralel, to organize ourselves 
		 * when we try to look for the state of the consumers. Let's give a random UniversalId to the name of the instance.
		 * 
		 * */
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, FraudDetectorService.class.getSimpleName() + "-" + UUID.randomUUID().toString());
				
		return properties;
	}

}
