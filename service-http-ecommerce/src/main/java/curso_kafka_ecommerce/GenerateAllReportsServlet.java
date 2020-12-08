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

public class GenerateAllReportsServlet extends HttpServlet 
{
	private final KafkaDispatcher<String> batchDispatcher = new KafkaDispatcher<>();

	@Override
	public void destroy() 
	{
		super.destroy();
		try {
			batchDispatcher.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		try 
		{
			batchDispatcher.send("SEND_MESSAGE_TO_ALL_USERS", "USER_GENERATE_READING_REPORT", "USER_GENERATE_READING_REPORT");
			System.out.println("Reports order generated for all users!!!");
			
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().println("Orders have been sent! ^^");
		} 
		catch (InterruptedException e) {
			throw new ServletException(e);
		} 
		catch (ExecutionException e) {
			throw new ServletException(e);
		}
	}	
}
