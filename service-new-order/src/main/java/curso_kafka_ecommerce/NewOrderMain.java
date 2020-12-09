package curso_kafka_ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import curso_kafka.models.Order;

public class NewOrderMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException 
	{
		try(var orderDispatcher = new KafkaDispatcher<Order>(NewOrderMain.class.getName()))
		{
			try(var emailDispatcher = new KafkaDispatcher<String>(NewOrderMain.class.getName()))
			{
				for(var i = 0; i < 10; i++) 
				{
					var orderId = UUID.randomUUID().toString();
					var amount = new BigDecimal(Math.random() * 5000 + 1);
					var userEmail = Math.random() + "@email.com";
					
					var order = new Order(orderId, amount, userEmail);
					var emailMessage = "Be welcome! We are processing your order :)";

					orderDispatcher.send("ECOMMERCE_NEW_ORDER", userEmail, order);
					emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userEmail, emailMessage);
				}
			}
		}	
	}
}
