package curso_kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

/*														CLASS 02_Explanations
 * 
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
 * The results of this table will be gathered after kafka commit's each changes. but, by default, that will happen only after MANY messages are consumed
 * We can change it adding a property MAX_POLL_RECORDS_CONFIG
 * 
 * 				GROUP                                                TOPIC               PARTITION 		 CURRENT-OFFSET  	 LOG-END-OFFSET  		LAG             CONSUMER-ID		 																									HOST            CLIENT-ID
	FraudDetectorService-13a3e53d-d04d-4115-9c47-5a5a8f478dc3 ECOMMERCE_NEW_ORDER 			0          			41              	68              27              consumer-FraudDetectorService-13a3e53d-d04d-4115-9c47-5a5a8f478dc3-1-ef8168ba-dcce-4689-b68c-31aa32c51f6b 		/192.168.0.4    consumer-FraudDetectorService-13a3e53d-d04d-4115-9c47-5a5a8f478dc3-1
	FraudDetectorService-13a3e53d-d04d-4115-9c47-5a5a8f478dc3 ECOMMERCE_NEW_ORDER 			1         			47              	47              0               consumer-FraudDetectorService-13a3e53d-d04d-4115-9c47-5a5a8f478dc3-1-ef8168ba-dcce-4689-b68c-31aa32c51f6b 		/192.168.0.4    consumer-FraudDetectorService-13a3e53d-d04d-4115-9c47-5a5a8f478dc3-1
	FraudDetectorService-13a3e53d-d04d-4115-9c47-5a5a8f478dc3 ECOMMERCE_NEW_ORDER 			2         			22              	48              26              consumer-FraudDetectorService-13a3e53d-d04d-4115-9c47-5a5a8f478dc3-1-ef8168ba-dcce-4689-b68c-31aa32c51f6b 		/192.168.0.4    consumer-FraudDetectorService-13a3e53d-d04d-4115-9c47-5a5a8f478dc3-1

				GROUP                                                TOPIC               PARTITION  		CURRENT-OFFSET   LOG-END-OFFSET  		LAG             CONSUMER-ID   																										HOST            CLIENT-ID
	FraudDetectorService-239f22a1-ac93-48e2-b225-85cb34639d1e ECOMMERCE_NEW_ORDER 			0          			33              	68              35              consumer-FraudDetectorService-239f22a1-ac93-48e2-b225-85cb34639d1e-1-8931d409-9005-4bb6-812a-1188d4a845f4 		/192.168.0.4    consumer-FraudDetectorService-239f22a1-ac93-48e2-b225-85cb34639d1e-1
	FraudDetectorService-239f22a1-ac93-48e2-b225-85cb34639d1e ECOMMERCE_NEW_ORDER 			1          			29              	47              18              consumer-FraudDetectorService-239f22a1-ac93-48e2-b225-85cb34639d1e-1-8931d409-9005-4bb6-812a-1188d4a845f4 		/192.168.0.4    consumer-FraudDetectorService-239f22a1-ac93-48e2-b225-85cb34639d1e-1
	FraudDetectorService-239f22a1-ac93-48e2-b225-85cb34639d1e ECOMMERCE_NEW_ORDER 			2          			48              	48               0               consumer-FraudDetectorService-239f22a1-ac93-48e2-b225-85cb34639d1e-1-8931d409-9005-4bb6-812a-1188d4a845f4 		/192.168.0.4    consumer-FraudDetectorService-239f22a1-ac93-48e2-b225-85cb34639d1e-1
 * 
 * Kafka's will rebalance by its own, following it's own properties */

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
		
		/*Actually, I've made a mistake in one of my tests. The NAME I can change is in property CLIENT_ID_CONFIG, NOT 
		* GROUP_ID_CONFIG. If I change THIS property, as I did before, we will create OTHER groups and not use the same 
		* group in different partitions (I know, I know, a rookie's mistake) */
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, FraudDetectorService.class.getSimpleName());
		
		/* NOW WE ARE GIVING A DIFFERENT NAME. So, the explain of why is bellow.
		 * 	We could also give a specific name to the instance, since we will execute many in paralel, to organize ourselves 
		 * 	when we try to look for the state of the consumers. Let's give a random UniversalId to the name of the instance.*/
		properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, FraudDetectorService.class.getSimpleName() + "-" + UUID.randomUUID().toString());
		
		/* With this configuration, the max of messages we are going to consume BEFORE executing the callback function
		 * will change. As default, we wait until ALL messages be consumed, but then it might take a while before it
		 * gives me a feedback */
		properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
		
		return properties;
	}

}
