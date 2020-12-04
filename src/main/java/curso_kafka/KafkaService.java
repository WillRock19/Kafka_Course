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

	KafkaService(String groupId, String topic, IConsumerFunction consumerFunction, Class<T> type) 
	{
		this(groupId, consumerFunction, type);	
		consumer.subscribe(Collections.singletonList(topic));
	}

	KafkaService(String groupId, Pattern topic, IConsumerFunction consumerFunction, Class<T> type) {
		this(groupId, consumerFunction, type);
		consumer.subscribe(topic);
	}

	private KafkaService(String groupId, IConsumerFunction consumerFunction, Class<T> type) 
	{
		this.consumerFunction = consumerFunction;
		this.consumer = new KafkaConsumer<String, T>(produceProperties(groupId, type));
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
	
	private Properties produceProperties(String groupID, Class<T> type) 
	{
		var properties = new Properties();
		
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");	
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		
		/* The use of GsonSerializer will throw an exception when we try to execute a message that is just a string,
		 * because a string is not a valid JSON, and the Gson will try to convert the message to a valid JSON.
		 * 
		 * Crap baskets, hum?
		 * 
		 * A way to deal with this is create a "shell" to represent our pure message, so it might be converted
		 * to a valid JSON (in this case, the message in question will be the message o email. So we will create
		 * a email class to represent it. 
		 */
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
		
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID);
		properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());	
		
		/* I'm changing the comment that was here before, because now we are getting the type of the data 
		 * from the consumers that instantiate this KafkaService */
		properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
		
		return properties;
	}
}
