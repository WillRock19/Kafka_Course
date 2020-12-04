package curso_kafka;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class NewOrderMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		var producer = new KafkaProducer<String, String>(producerProperties());
		
		//Executing this code 100 times so we can see how Kafka will distribute the messages
		for(var i = 0; i < 100; i++) {
			//Let's create a key that will change for every user (pretend it is a user's id);
			var key = UUID.randomUUID().toString();
			var value = key + ",666,1999";
			var email = "welcome! We are processing your order :)";
			
			//Using the key so the KAfka's algorithm will rebalance the messages between multiple partitions
			var record = new ProducerRecord<String, String>("ECOMMERCE_NEW_ORDER", key, value);
			var emailRecord = new ProducerRecord<String, String>("ECOMMERCE_SEND_EMAIL", key, email);
			
			producer.send(record, printResultCallback()).get();		
			producer.send(emailRecord, printResultCallback()).get();
		}
	}

	private static Callback printResultCallback() {
		return (data, exception) -> {
			if(exception != null) 
			{
				exception.printStackTrace();
				return;
			}
			
			System.out.println(data.topic() + "::: partition: " + data.partition() + "/offset: " + data.offset() + "/timestamp: " + data.timestamp());
		};
	}

	private static Properties producerProperties() {
		var properties = new Properties();
		
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		
		return properties;
	}

}
