package curso_kafka.consumer;

import java.util.Map;
import java.util.concurrent.Callable;

import curso_kafka.consumer.interfaces.IServiceFactory;

public class ServiceProvider<T> implements Callable<Void> {

	private final IServiceFactory<T> factory;
	
	public ServiceProvider(IServiceFactory<T> factory) 
	{
		this.factory = factory;
	}
	
	public Void call() throws Exception 
	{
		var serviceToRun = factory.create();
		
		try(var kafkaService = new KafkaService<>(serviceToRun.getConsumerGroup(), serviceToRun.getTopic(), 
												  serviceToRun::parseRecord, Map.of()))
		{
			kafkaService.run();
		}
		
		return null;
	}
}
