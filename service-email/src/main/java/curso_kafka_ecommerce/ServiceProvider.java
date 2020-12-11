package curso_kafka_ecommerce;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import curso_kafka.consumer.KafkaService;

public class ServiceProvider {

	public <T> void run(IServiceFactory<T> factory) throws InterruptedException, ExecutionException, IOException 
	{
		var myService = factory.create();
		
		try(var kafkaService = new KafkaService<>(myService.getTopic(), myService.getConsumerGroup(), 
												  myService::parseRecord, Map.of()))
		{
			kafkaService.run();
		}	
	}

}
