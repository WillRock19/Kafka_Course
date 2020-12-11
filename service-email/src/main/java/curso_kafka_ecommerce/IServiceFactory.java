package curso_kafka_ecommerce;

public interface IServiceFactory<T>
{
	ConsumerService<T> create();
}
