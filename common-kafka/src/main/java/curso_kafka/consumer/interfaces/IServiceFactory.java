package curso_kafka.consumer.interfaces;

public interface IServiceFactory<T>
{
	IConsumerService<T> create() throws Exception;
}
