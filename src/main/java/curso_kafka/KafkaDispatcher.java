package curso_kafka;

import java.io.Closeable;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

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
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
						
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
