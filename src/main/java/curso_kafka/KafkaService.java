package curso_kafka;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import curso_kafka.services.GsonDeserializer;

class KafkaService<T> implements Closeable
{
	private final KafkaConsumer<String, T> consumer;
	private final IConsumerFunction consumerFunction;

	KafkaService(String groupId, String topic, IConsumerFunction consumerFunction) 
	{
		this(groupId, consumerFunction);	
		consumer.subscribe(Collections.singletonList(topic));
	}

	KafkaService(String groupId, Pattern topic, IConsumerFunction consumerFunction) {
		this(groupId, consumerFunction);
		consumer.subscribe(topic);
	}

	private KafkaService(String groupId, IConsumerFunction consumerFunction) 
	{
		this.consumerFunction = consumerFunction;
		this.consumer = new KafkaConsumer<String, T>(produceProperties(groupId));
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
		
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
		
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID);
		properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());	
		
		
		/* Property we are creating to tell to deserializer which class name it is going to deserialize our data in.
		 * Ass you can see, by default we are deserializing as a string. */
		properties.setProperty(GsonDeserializer.TYPE_CONFIG, String.class.getName());
		
		return properties;
	}
}
