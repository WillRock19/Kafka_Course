package curso_kafka_ecommerce;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import curso_kafka.services.GsonSerializer;

class KafkaDispatcher<T> implements Closeable
{
	private final KafkaProducer<String, Message<T>> producer;
	
	KafkaDispatcher() 
	{
		this.producer = new KafkaProducer<String, Message<T>>(produceProperties());	
	}

	public void send(String topic, String key, CorrelationId correlationId, T payload) throws InterruptedException, ExecutionException 
	{
		var future = sendAsync(topic, key, correlationId, payload);
		future.get();
	}
	
	public Future<RecordMetadata> sendAsync(String topic, String key, CorrelationId correlationId, T payload) throws InterruptedException, ExecutionException 
	{
		var message = new Message<T>(correlationId, payload);
		var record = new ProducerRecord<String, Message<T>>(topic, key, message);
		
		return producer.send(record, printResultCallback());
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
