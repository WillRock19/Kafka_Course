package curso_kafka_ecommerce;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.consumer.ServiceRunner;
import curso_kafka.consumer.interfaces.IConsumerService;
import curso_kafka.dispatcher.KafkaDispatcher;
import curso_kafka.dispatcher.Message;
import curso_kafka.models.Order;

public class EmailNewOrderService implements IConsumerService<Order> {

	private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException 
	{
		var numberOfThreads = 1;
		new ServiceRunner<>(EmailNewOrderService::new).start(numberOfThreads);
	}

	@Override
	public String getTopic() {
		return "ECOMMERCE_NEW_ORDER";
	}

	@Override
	public String getConsumerGroup() {
		return EmailNewOrderService.class.getTypeName();
	}

	public void parseRecord(ConsumerRecord<String, Message<Order>> record) throws InterruptedException, ExecutionException 
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
