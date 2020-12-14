package curso_kafka_ecommerce;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.consumer.ServiceRunner;
import curso_kafka.consumer.interfaces.IConsumerService;
import curso_kafka.dispatcher.Message;
import curso_kafka.models.Order;

public class CreateUserService implements IConsumerService<Order> {
	
	private Connection connection;

	CreateUserService() throws SQLException {
		initializeDatabaseConnection();	
		createUsersTable();
	}
	
	public static void main(String[] args) throws SQLException, InterruptedException, ExecutionException, IOException 
	{
		var numberOfThreads = 1;
		new ServiceRunner<>(CreateUserService::new).start(numberOfThreads);
	}
	
	@Override
	public String getTopic() {
		return "ECOMMERCE_NEW_ORDER";
	}

	@Override
	public String getConsumerGroup() {
		return CreateUserService.class.getTypeName();
	}

	public void parseRecord(ConsumerRecord<String, Message<Order>> record) throws InterruptedException, ExecutionException, SQLException 
	{
		var message = record.value();
		var order = message.getPayload();
		
		System.out.println("--------------------------------------------");
		System.out.println("Processing new order. Checking for new user...");
		System.out.println("-> " + order);	
		
		if(isNewUser(order.getEmail())) 
		{	
			insertNewUser(order.getEmail());
			System.out.println("Usuário " + order.getEmail() + " adicionado com sucesso :)");
		}
	}
	
	private void initializeDatabaseConnection() throws SQLException {
		var connectionString = "jdbc:sqlite:target/users_database.db";
		connection = DriverManager.getConnection(connectionString);
	}
	
	private void createUsersTable() {
		try {
		connection.createStatement().execute("create table Users(" +
				"uuid varchar(200) primary key," + 
				"email varchar(200))");	
		}
		catch(SQLException ex) {
			/* This will be to deal with the exception when the database ALREADY EXISTS (but 
			 * this might catch other exceptions. Since in the class we didn't care about 
			 * this situation, I won't care here for now)
			 */
			ex.printStackTrace();
		}
	}
	
	private void insertNewUser(String email) throws SQLException {
		var insertStatement = connection.prepareStatement("insert into Users (uuid, email) values (?,?)");
		
		insertStatement.setString(1, UUID.randomUUID().toString());
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
