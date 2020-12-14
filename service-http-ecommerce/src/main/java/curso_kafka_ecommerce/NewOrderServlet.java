package curso_kafka_ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import curso_kafka.database.OrdersDatabase;
import curso_kafka.dispatcher.CorrelationId;
import curso_kafka.dispatcher.KafkaDispatcher;
import curso_kafka.models.Order;

public class NewOrderServlet extends HttpServlet {

	private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

	@Override
	public void destroy() 
	{
		super.destroy();
		try {
			orderDispatcher.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		try {
			/* Changing the way we get the orderID will guarantee that EVERY client that sends an HTTP Request MUST 
			 * send an UUID to my server (that's non negotiable SPOCK). */
			var orderId = req.getParameter("uuid");
			
			var amount = new BigDecimal(req.getParameter("amount"));				
			var userEmail = req.getParameter("email");
			var order = new Order(orderId, amount, userEmail);
			
			try(var database = new OrdersDatabase())
			{
				if(database.saveNewOrder(order)) 
				{
					orderDispatcher.send("ECOMMERCE_NEW_ORDER", userEmail, 
							new CorrelationId(NewOrderServlet.class.getSimpleName()), order);
					
					System.out.println("New order sent successfuly");	
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getWriter().println("New order sent! ^^");
				}
				else 
				{
					System.out.println("Old order received");			
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getWriter().println("Old order will not be processed again.");
				}
			}	
		} 
		catch (InterruptedException | ExecutionException | SQLException e) {
			throw new ServletException(e);
		}
	}	
}
