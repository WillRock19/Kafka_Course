package curso_kafka.models;

import java.math.BigDecimal;

public class Order 
{
	private final String userId, orderId;
	private final BigDecimal amount;
	
	public Order(String userId, String orderId, BigDecimal amount) 
	{
		this.userId = userId;
		this.orderId = orderId;
		this.amount = amount;
	}
	
	/* Now we are just modifying this version of Order, because it is the only one we need to modify
	 * (the version inside the service-new-order does not need to have a getAmount method, so this is
	 * an advantage of separating a model for each service)
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	
	public String getUserId() {
		return userId;
	}
	
	@Override
	public String toString() {
		return String.format("Order{ userId='%s', orderId='%s', amount=%s };", userId, orderId, amount);
	}


}
