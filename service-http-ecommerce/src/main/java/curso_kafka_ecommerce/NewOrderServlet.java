package curso_kafka_ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import curso_kafka.dispatcher.CorrelationId;
import curso_kafka.dispatcher.KafkaDispatcher;
import curso_kafka.models.Order;

public class NewOrderServlet extends HttpServlet {

	private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
	private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

	@Override
	public void destroy() 
	{
		super.destroy();
		try {
			orderDispatcher.close();
			emailDispatcher.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		try {
			var orderId = UUID.randomUUID().toString();
			var amount = new BigDecimal(req.getParameter("amount"));				
			var userEmail = req.getParameter("email");
			
			var order = new Order(orderId, amount, userEmail);
			var emailMessage = "Be welcome! We are processing your order :)";

			orderDispatcher.send("ECOMMERCE_NEW_ORDER", userEmail, new CorrelationId(NewOrderServlet.class.getSimpleName()), order);
			emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userEmail, new CorrelationId(NewOrderServlet.class.getSimpleName()), emailMessage);
			
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
