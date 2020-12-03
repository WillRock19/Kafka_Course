package curso_kafka;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class NewOrderMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		var producer = new KafkaProducer<String, String>(producerProperties());
		var value = "9919,666,1999";
		
		//Here the first parameter is the topic of the record
		//The second is the record's key
		//The third is the record's value
		var record = new ProducerRecord<String, String>("ECOMMERCE_NEW_ORDER", value, value);
		
		//The .get() is a method that forces the program to wait for the Future<> to be resolved
		//The second parameter is a callback function that will be executed when the future is resolved
		producer.send(record, (data, exception) -> {
			if(exception != null) 
			{
				exception.printStackTrace();
				return;
			}
			
			System.out.println(data.topic() + "::: partition: " + data.partition() + "/offset: " + data.offset() + "/timestamp: " + data.timestamp());
		})
		.get();
	}

	private static Properties producerProperties() {
		var properties = new Properties();
		
		//Defining in what IP address the Kafka is running
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		
		//Defining serializer from string to bytes to keys and values, since them both will be strings,
		//we need to define what serializer kaff will use to serialize the strings to bytes so it can work with it
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		return properties;
	}

}
