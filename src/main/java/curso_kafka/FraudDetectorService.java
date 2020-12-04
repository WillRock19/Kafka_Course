package curso_kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.models.Order;

public class FraudDetectorService {

	public static void main(String[] args) {
		var fraudService = new FraudDetectorService();
		
		try(var service = new KafkaService(
				FraudDetectorService.class.getTypeName(), 
				"ECOMMERCE_NEW_ORDER",  
				fraudService::parseRecord,
				Order.class)) //FraudService's type to deserialize will be an object of type Order
		{
			service.run();
		}	
	}

	private void parseRecord(ConsumerRecord<String, Order> record) 
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
		System.out.println("Order processed! :)");
	}
}
