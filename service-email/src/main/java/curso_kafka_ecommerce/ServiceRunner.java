package curso_kafka_ecommerce;

import java.util.concurrent.Executors;

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
