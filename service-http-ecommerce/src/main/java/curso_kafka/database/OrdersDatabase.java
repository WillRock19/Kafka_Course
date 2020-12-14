package curso_kafka.database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

import curso_kafka.models.Order;

public class OrdersDatabase implements Closeable 
{
	private final LocalDatabase database;
	
	public OrdersDatabase() throws SQLException 
	{
		this.database = new LocalDatabase("orders_database");
		
		/*In this class we'll save only the UUID, but the ideal would be save all the message's information*/
		this.database.createIfNotExists("create table Orders (" + 
				"uuid varchar(200) primary key);");
	}

	public boolean saveNewOrder(Order order) throws SQLException 
	{
		if(alreadyProcessed(order))  {
			return false;
		}
		
		this.database.Update("insert into Orders (uuid) values (?)", order.getOrderId());
		return true;
	}
	
	private boolean alreadyProcessed(Order order) throws SQLException 
	{
		var results = this.database.ExecuteQuery("SELECT uuid from Orders where uuid = ? limit 1", order.getOrderId());
		return results.next();
	}

	@Override
	public void close() throws IOException 
	{
		try {
			this.database.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}	
	}
}
