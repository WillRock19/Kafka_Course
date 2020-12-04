package curso_kafka;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException 
	{
		try(var dispatcher = new KafkaDispatcher())
		{
			for(var i = 0; i < 10; i++) 
			{
				var key = UUID.randomUUID().toString();
				var value = key + ",666,1999";
				var email = "welcome! We are processing your order :)";

				dispatcher.send("ECOMMERCE_NEW_ORDER", key, value);
				dispatcher.send("ECOMMERCE_SEND_EMAIL", key, email);
			}
		}	
	}
}
