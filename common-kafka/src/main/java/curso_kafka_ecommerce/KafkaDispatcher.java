package curso_kafka_ecommerce;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import curso_kafka.services.GsonSerializer;

class KafkaDispatcher<T> implements Closeable
{
	private final KafkaProducer<String, T> producer;

	KafkaDispatcher() 
	{
		this.producer = new KafkaProducer<String, T>(produceProperties());	
	}

	public void send(String topic, String key, T value) throws InterruptedException, ExecutionException 
	{
		var record = new ProducerRecord<String, T>(topic, key, value);		
		producer.send(record, printResultCallback()).get();
	}
	
	@Override
	public void close() throws IOException {
		this.producer.close();	
	}
	
	private Properties produceProperties() {
		var properties = new Properties();
		
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		
		/* Now that we are using a generic type, we can't keep using the stringSerializer, since it only serialize 
		 * strings. We can use another formats, though. Let's try to use a JSON serializer, saw we? Let's use, for
		 * that, a library called GSON.
		*/
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
		
		/* Making sure every Replica will receive a update with the last message, from the Leader, before the broker
		 * Leader return an acknowledge to our .get(). 
		 */
		properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		
		return properties;
	}
	
	private Callback printResultCallback() 
	{
		return (data, exception) -> {
			if(exception != null) 
			{
				exception.printStackTrace();
				return;
			}
			
			System.out.println(data.topic() + "::: partition: " + data.partition() + "/offset: " + data.offset() + "/timestamp: " + data.timestamp());
		};
	}
}
