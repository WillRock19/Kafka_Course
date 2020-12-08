package curso_kafka_ecommerce;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HttpEcommerceService {

	public static void main(String[] args) throws Exception {
		var server = new Server(8080);
		var contextToHandleRequests = new ServletContextHandler();
		contextToHandleRequests.setContextPath("/");
		contextToHandleRequests.addServlet(new ServletHolder(new NewOrderServlet()), "/new");		
		contextToHandleRequests.addServlet(new ServletHolder(new GenerateAllReportsServlet()), "/admin/generate/reports");
		
		server.setHandler(contextToHandleRequests);
		
		/*Starting the server*/
		server.start();

		/*Waiting the server to end before closes the application*/
		server.join();
	}
}
