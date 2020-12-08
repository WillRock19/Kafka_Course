package curso_kafka_ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.Source;

import curso_kafka.models.Order;
import curso_kafka.models.User;

public class GenerateAllReportsServlet extends HttpServlet 
{
	private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();

	@Override
	public void destroy() 
	{
		super.destroy();
		try {
			userDispatcher.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		try {
			for(User user : users) 
			{
				/* If we use as key the UUID of the user, the algorithm will execute reports of same
				 * user one after another, since it won't make to partitions execute at same time (remember 
				 * that the algorithm uses the key to understand how to execute the data in different
				 * partitions... so, two values with the same key will always be executed by the same
				 * partition, one after another.
				 */
				userDispatcher.send("USER_GENERATE_READING_REPORT", user.getUUID(), user);
			}
			
			System.out.println("Reports generated for all users!!!");
			
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
