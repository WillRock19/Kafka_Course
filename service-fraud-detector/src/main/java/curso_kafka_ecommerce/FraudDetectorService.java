package curso_kafka_ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.consumer.ServiceRunner;
import curso_kafka.consumer.interfaces.IConsumerService;
import curso_kafka.database.LocalDatabase;
import curso_kafka.dispatcher.KafkaDispatcher;
import curso_kafka.dispatcher.Message;
import curso_kafka.models.Order;

public class FraudDetectorService implements IConsumerService<Order> {

	private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();
	private final LocalDatabase database;

	public FraudDetectorService() throws SQLException 
	{
		this.database = new LocalDatabase("fraud_database");
		createFraudsTable();
	}
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException 
	{
		var numberOfThreads = 1;
		new ServiceRunner<>(FraudDetectorService::new).start(numberOfThreads);
	}
	
	@Override
	public String getTopic() {
		return "ECOMMERCE_NEW_ORDER";
	}

	@Override
	public String getConsumerGroup() {
		return FraudDetectorService.class.getTypeName();
	}

	public void parseRecord(ConsumerRecord<String, Message<Order>> record) throws InterruptedException, ExecutionException, SQLException 
	{
		System.out.println("--------------------------------------------");
		System.out.println("Processing new order. Checking for frauds...");
		System.out.println("-> " + record.key());
		System.out.println("-> " + record.value());
		System.out.println("-> " + record.partition());
		System.out.println("-> " + record.offset());
		
		try{
			Thread.sleep(5000);	
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		var message = record.value();
		var order = message.getPayload();
		
		if(alreadyProcessed(order)) {
			System.out.println(String.format("The order %s was already processed.", order.getOrderId()));
			return;
		}
		
		if(orderIsFraud(order)) 
		{
			System.out.println("Order is a fraud! You phony!!! ¬¬");

			/* We are using the OrderId created outside kafka's broker (in the NewOrderServlet) as a unique Id to
			 * our messages
			 */
			this.database.Update("INSERT INTO Orders (uuid, is_fraud) VALUES (?,true)", order.getOrderId());
			
			orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(), 
					message.getCorrelationId().continueWith(FraudDetectorService.class.getSimpleName()), order);
		}
		else {
			System.out.println("Aproved: " + order);
			
			/* We are using the OrderId created outside kafka's broker (in the NewOrderServlet) as a unique Id to
			 * our messages
			 */
			this.database.Update("INSERT INTO Orders (uuid, is_fraud) VALUES (?,false)", order.getOrderId());
			
			orderDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(),  
					message.getCorrelationId().continueWith(FraudDetectorService.class.getSimpleName()), order);
		}
	}
	
	private void createFraudsTable() 
	{
		this.database.createIfNotExists("create table Orders(" +
				"uuid varchar(200) primary key," + 
				"is_fraud boolean)");
	}
	
	private boolean orderIsFraud(Order order) 
	{
		return order.getAmount().compareTo(new BigDecimal("4500"))  >= 0;
	}

	private boolean alreadyProcessed(Order order) throws SQLException 
	{
		var results = this.database.ExecuteQuery("SELECT uuid from Orders where uuid = ? limit 1", order.getOrderId());
		return results.next();
	}
}
