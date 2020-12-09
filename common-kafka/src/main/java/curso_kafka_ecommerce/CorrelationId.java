package curso_kafka_ecommerce;

import java.util.UUID;

public class CorrelationId 
{
	private final String id;
	
	public CorrelationId(String title) {
		this.id = title + "(" + UUID.randomUUID().toString() + ")";
	}
	
	@Override
	public String toString() {
		return "CorrelationId { id='" + id + "'}";
	}

	public CorrelationId continueWith(String newId) {
		return new CorrelationId(id + "-" + newId);
	}
}
