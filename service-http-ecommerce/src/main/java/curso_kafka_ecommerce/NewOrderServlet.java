package curso_kafka_ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import curso_kafka.models.Order;

public class NewOrderServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		try(var orderDispatcher = new KafkaDispatcher<Order>())
		{
			try(var emailDispatcher = new KafkaDispatcher<String>())
			{
					var orderId = UUID.randomUUID().toString();
					var amount = new BigDecimal(Math.random() * 5000 + 1);
					var userEmail = Math.random() + "@email.com";
					
					var order = new Order(orderId, amount, userEmail);
					var emailMessage = "Be welcome! We are processing your order :)";

					orderDispatcher.send("ECOMMERCE_NEW_ORDER", userEmail, order);
					emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userEmail, emailMessage);
					
					System.out.println("New order sent successfuly");
					
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getWriter().println("New order sent! ^^");
			} 
			catch (InterruptedException e) {
				throw new ServletException(e);
			} 
			catch (ExecutionException e) {
				throw new ServletException(e);
			}
		}	
	}
}
