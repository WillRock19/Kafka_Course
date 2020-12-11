package curso_kafka_ecommerce;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.consumer.KafkaService;
import curso_kafka.dispatcher.KafkaDispatcher;
import curso_kafka.dispatcher.Message;
import curso_kafka.models.User;

public class BatchSendMessageService {

	private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();
	private Connection connection;

	BatchSendMessageService() throws SQLException {
		initializeDatabaseConnection();	
		createUsersTable();
	}
	
	public static void main(String[] args) throws SQLException, InterruptedException, ExecutionException, IOException {
		var batchService = new BatchSendMessageService();
		
		try(var service = new KafkaService<>(
				BatchSendMessageService.class.getTypeName(), 
				"ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS",  
				batchService::parseRecord,
				Map.of()))
		{
			service.run();
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
			ex.printStackTrace();
		}
	}

	private void parseRecord(ConsumerRecord<String, Message<String>> record) throws InterruptedException, ExecutionException, SQLException 
	{
		var message = record.value();
		
		System.out.println("--------------------------------------------");
		System.out.println("Processing new batch...");
		System.out.println("-> Topic: " + message.getPayload());	
		
		for(User user : getAllUsers()) 
		{
			userDispatcher.sendAsync(message.getPayload(), user.getUUID(), 
					message.getCorrelationId().continueWith(BatchSendMessageService.class.getSimpleName()), user);
			
			System.out.println("Acho que enviei para " + user);
		}

	}
	
	private ArrayList<User> getAllUsers() throws SQLException {
		var results = connection.prepareStatement("SELECT uuid from Users").executeQuery();
		var users = new ArrayList<User>();
		
		while(results.next()) {
			users.add(new User(results.getString(1)));
		}
		
		return users;
	}
}
