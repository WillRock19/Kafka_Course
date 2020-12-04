package curso_kafka;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

class KafkaService implements Closeable
{
	private final KafkaConsumer<String, String> consumer;
	private final IConsumerFunction consumerFunction;

	KafkaService(String groupId, String topic, IConsumerFunction consumerFunction) 
	{
		this.consumerFunction = consumerFunction;
		this.consumer = new KafkaConsumer<String, String>(produceProperties(groupId));
		
		consumer.subscribe(Collections.singletonList(topic));
	}

	void run() 
	{
		while(true) 
		{
			//Defining the amount of time the subscriber will be looking for a data inside Kafka before proceed code execution
			var records = consumer.poll(Duration.ofMillis(100));
			
			if(!records.isEmpty()) {
				System.out.println("Founded " + records.count() + " registers!");
				
				for(var record : records) 
				{
					consumerFunction.consume(record);
				}
			}
		}	
	}
	
	@Override
	public void close() {
		this.consumer.close();
	}
	
	private Properties produceProperties(String groupID) {
		var properties = new Properties();
		
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		

		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID);
		properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());	
		//properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
				
		return properties;
	}
}
