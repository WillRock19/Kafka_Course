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

	private void parseRecord(ConsumerRecord<String, Order> record) throws InterruptedException, ExecutionException, SQLException 
	{
		var order = record.value();
		
		System.out.println("--------------------------------------------");
		System.out.println("Processing new order. Checking for frauds...");
		System.out.println("-> " + order);	
		
		if(isNewUser(order.getEmail())) 
		{	
			insertNewUser(order.getEmail());
			System.out.println("Usu�rio adicionado com sucesso :)");
		}
	}

	private void insertNewUser(String email) throws SQLException {
		var insertStatement = connection.prepareStatement("insert into Users (uuid, email) values (?,?)");
		
		insertStatement.setString(1, "uuid");
		insertStatement.setString(2, email);
		
		insertStatement.execute();
	}

	private boolean isNewUser(String email) throws SQLException {
		var exists = connection.prepareStatement("select uuid from Users where email = ? limit 1");
		exists.setString(1, email);
		
		var results = exists.executeQuery();
		
		/* If next() return a new line, the register exists. If it doesn't, it means it's a new one.*/
		return !results.next();
	}
}
