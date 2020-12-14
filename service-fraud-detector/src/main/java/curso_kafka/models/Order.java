package curso_kafka.models;

import java.math.BigDecimal;

public class Order 
{
	private final String email, orderId;
	private final BigDecimal amount;
	
	public Order(String email, String orderId, BigDecimal amount) 
	{
		this.email = email;
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
	
	public String getEmail() {
		return email;
	}
	
	public String getOrderId() {
		return orderId;
	}
	
	@Override
	public String toString() {
		return String.format("Order{ orderId='%s', amount=%s, email='%s' };", orderId, amount, email);
	}
}
