package curso_kafka_ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import curso_kafka.dispatcher.CorrelationId;
import curso_kafka.dispatcher.KafkaDispatcher;
import curso_kafka.models.Order;

public class NewOrderMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException 
	{
		try(var orderDispatcher = new KafkaDispatcher<Order>())
		{
			for(var i = 0; i < 10; i++) 
			{
				var orderId = UUID.randomUUID().toString();
				var amount = new BigDecimal(Math.random() * 5000 + 1);
				var userEmail = Math.random() + "@email.com";
				
				var order = new Order(orderId, amount, userEmail);
				var correlationId = new CorrelationId(NewOrderMain.class.getName());
				
				orderDispatcher.send("ECOMMERCE_NEW_ORDER", userEmail, correlationId, order);
			}
		}	
	}
}
