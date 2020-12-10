package curso_kafka_ecommerce;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import curso_kafka.services.GsonDeserializer;

class KafkaService<T> implements Closeable
{
	private final KafkaConsumer<String, Message<T>> consumer;
	private final IConsumerFunction consumerFunction;

	KafkaService(String groupId, String topic, IConsumerFunction<T> consumerFunction, Map<String, String> extraPropertiesToUse) 
	{
		this(groupId, consumerFunction, extraPropertiesToUse);	
		consumer.subscribe(Collections.singletonList(topic));
	}

	KafkaService(String groupId, Pattern topic, IConsumerFunction<T> consumerFunction, Map<String, String> extraPropertiesToUse) {
		this(groupId, consumerFunction, extraPropertiesToUse);
		consumer.subscribe(topic);
	}

	private KafkaService(String groupId, IConsumerFunction<T> consumerFunction, Map<String, String> extraPropertiesToUse) 
	{
		this.consumerFunction = consumerFunction;
		this.consumer = new KafkaConsumer<String, Message<T>>(produceProperties(groupId, extraPropertiesToUse));
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
					try {
						consumerFunction.consume(record);
					} 
					catch (Exception e) {
						//We'll threat the consume exception as a log for now
						e.printStackTrace();
					}
				}
			}
		}	
	}
	
	@Override
	public void close() {
		this.consumer.close();
	}
	
	private Properties produceProperties(String groupID, Map<String, String> extraPropertiesToUse) 
	{
		var properties = new Properties();
		
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");	
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
		
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID);
		properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());	
		
		properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
		
		properties.putAll(extraPropertiesToUse);
		
		return properties;
	}
}
