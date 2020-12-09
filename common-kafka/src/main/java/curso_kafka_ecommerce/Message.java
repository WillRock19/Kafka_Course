package curso_kafka_ecommerce;

public class Message<T>
{
	private final T payload;
	private final CorrelationId id;

	public Message(CorrelationId id, T payload) {
		this.id = id;
		this.payload = payload;
	}
	
	@Override
	public String toString() {
		return "Message { id=" + id + ",payload=" + payload + "}";
	}
}
