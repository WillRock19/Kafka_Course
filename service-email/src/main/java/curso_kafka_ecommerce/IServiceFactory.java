package curso_kafka_ecommerce;

public interface IServiceFactory<T>
{
	IConsumerService<T> create();
}
