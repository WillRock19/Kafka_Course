package curso_kafka.consumer;

import java.util.concurrent.Executors;

import curso_kafka.consumer.interfaces.IServiceFactory;

public class ServiceRunner<T> 
{
	private final ServiceProvider<T> provider;

	public ServiceRunner(IServiceFactory<T> factory) 
	{
		this.provider = new ServiceProvider<>(factory);
	}
	
	public void start(int numberOfThreads) 
	{
		var poolOfThreads = Executors.newFixedThreadPool(numberOfThreads);
		
		for(int index = 0; index <= numberOfThreads; index++)
			poolOfThreads.submit(this.provider);
	}
}
