package curso_kafka_ecommerce;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.models.Order;

public class FraudDetectorService {

	private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();
	
	public static void main(String[] args) {
		var fraudService = new FraudDetectorService();
		
		try(var service = new KafkaService<>(
				FraudDetectorService.class.getTypeName(), 
				"ECOMMERCE_NEW_ORDER",  
				fraudService::parseRecord,
				Order.class,
				new HashMap<>()))
		{
			service.run();
		}	
	}

	private void parseRecord(ConsumerRecord<String, Message<Order>> record) throws InterruptedException, ExecutionException 
	{
		System.out.println("--------------------------------------------");
		System.out.println("Processing new order. Checking for frauds...");
		System.out.println("-> " + record.key());
		System.out.println("-> " + record.value());
		System.out.println("-> " + record.partition());
		System.out.println("-> " + record.offset());
		
		try{
			Thread.sleep(5000);	
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		var message = record.value();
		var order = message.getPayload();
		
		if(orderIsFraud(order)) {
			System.out.println("Order is a fraud! You phony!!! ¬¬");
			
			orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), 
					new CorrelationId(FraudDetectorService.class.getSimpleName()), order);
		}
		else {
			System.out.println("Aproved: " + order);
			
			orderDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(),  
					new CorrelationId(FraudDetectorService.class.getSimpleName()), order);
		}
	}
	
	private boolean orderIsFraud(Order order) {
		return order.getAmount().compareTo(new BigDecimal("4500"))  >= 0;
	}
}
