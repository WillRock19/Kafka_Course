package curso_kafka_ecommerce;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import curso_kafka.consumer.KafkaService;

public class ServiceProvider<T> implements Callable<Void> {

	private final IServiceFactory<T> factory;
	
	public ServiceProvider(IServiceFactory<T> factory) 
	{
		this.factory = factory;
	}
	
	public Void call() throws InterruptedException, ExecutionException, IOException 
	{
		var serviceToRun = factory.create();
		
		try(var kafkaService = new KafkaService<>(serviceToRun.getTopic(), serviceToRun.getConsumerGroup(), 
												  serviceToRun::parseRecord, Map.of()))
		{
			kafkaService.run();
		}
		
		return null;
	}
}
