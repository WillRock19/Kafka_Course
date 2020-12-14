package curso_kafka_ecommerce;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import curso_kafka.consumer.ServiceRunner;
import curso_kafka.consumer.interfaces.IConsumerService;
import curso_kafka.database.LocalDatabase;
import curso_kafka.dispatcher.Message;
import curso_kafka.models.Order;

public class CreateUserService implements IConsumerService<Order> 
{
	private final LocalDatabase database;

	CreateUserService() throws SQLException 
	{
		this.database = new LocalDatabase("users_database");
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
	
	private void createUsersTable() 
	{
		this.database.createIfNotExists("create table Users(" +
				"uuid varchar(200) primary key," + 
				"email varchar(200))");
	}
	
	private void insertNewUser(String email) throws SQLException 
	{
		var uuid = UUID.randomUUID().toString();	
		this.database.Update("insert into Users (uuid, email) values (?,?)", uuid, email);
	}

	private boolean isNewUser(String email) throws SQLException 
	{
		var results = this.database.ExecuteQuery("select uuid from Users where email = ? limit 1", email);

		/* If next() return a new line, the register exists. If it doesn't, it means it's a new one.*/
		return !results.next();
	}
}
