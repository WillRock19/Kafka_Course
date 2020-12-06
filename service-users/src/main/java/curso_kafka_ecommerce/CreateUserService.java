package curso_kafka_ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.models.Order;

public class CreateUserService {
	
	private Connection connection;

	CreateUserService() throws SQLException {
		initializeDatabaseConnection();
		createUsersTable();
	}
	
	public static void main(String[] args) throws SQLException {
		var createUserService = new CreateUserService();
		
		try(var service = new KafkaService<>(
				CreateUserService.class.getTypeName(), 
				"ECOMMERCE_NEW_ORDER",  
				createUserService::parseRecord,
				Order.class,
				new HashMap<>()))
		{
			service.run();
		}	
	}
	
	private void initializeDatabaseConnection() throws SQLException {
		var connectionString = "jdbc:sqlite:target/users_database.db";
		connection = DriverManager.getConnection(connectionString);
	}
	
	private void createUsersTable() throws SQLException {
		connection.createStatement().execute("create table Users(" +
				"uuid varchar(200) primary key" + 
				"email varchar(200))");		
	}

	private void parseRecord(ConsumerRecord<String, Order> record) throws InterruptedException, ExecutionException 
	{
		var order = record.value();
		
		System.out.println("--------------------------------------------");
		System.out.println("Processing new order. Checking for frauds...");
		System.out.println("-> " + order);	
		
		//if(isNewEmail(order.getEmail())) {	}
	}
}
