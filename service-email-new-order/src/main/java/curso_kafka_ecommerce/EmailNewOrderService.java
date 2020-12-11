package curso_kafka_ecommerce;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.consumer.KafkaService;
import curso_kafka.dispatcher.KafkaDispatcher;
import curso_kafka.dispatcher.Message;
import curso_kafka.models.Order;

public class EmailNewOrderService {

	private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		var fraudService = new EmailNewOrderService();
		
		try(var service = new KafkaService<>(
				EmailNewOrderService.class.getTypeName(), 
				"ECOMMERCE_NEW_ORDER",  
				fraudService::parseRecord,
				new HashMap<>()))
		{
			service.run();
		}	
	}

	private void parseRecord(ConsumerRecord<String, Message<Order>> record) throws InterruptedException, ExecutionException 
	{
		System.out.println("--------------------------------------------");
		System.out.println("Processing new order, preparing e-mail...");
		System.out.println("-> " + record.key());
		System.out.println("-> " + record.value());
		
		var message = record.value();
		var order = message.getPayload();
		var emailMessage = "Be welcome! We are processing your order :)";
		
		emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(), 
				message.getCorrelationId().continueWith(EmailNewOrderService.class.getSimpleName()), emailMessage);
	}
}
