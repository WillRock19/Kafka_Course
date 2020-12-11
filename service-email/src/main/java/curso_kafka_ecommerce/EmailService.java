package curso_kafka_ecommerce;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.dispatcher.Message;

public class EmailService implements IConsumerService<String> {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException 
	{
		new ServiceProvider<>(EmailService::new).call();
	}
	
	public String getTopic() {
		return "ECOMMERCE_SEND_EMAIL";
	}
	
	public String getConsumerGroup() {
		return EmailService.class.getSimpleName();
	}

	public void parseRecord(ConsumerRecord<String, Message<String>> record) 
	{
		System.out.println("--------------------------------------------");
		System.out.println("Sending email...");
		System.out.println("-> " + record.key());
		System.out.println("-> " + record.value());
		System.out.println("-> " + record.partition());
		System.out.println("-> " + record.offset());
		
		try{
			Thread.sleep(1000);	
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("E-mail sended ^^");
	}
}