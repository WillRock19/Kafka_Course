package curso_kafka.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LocalDatabase 
{
	private Connection connection;

	public LocalDatabase(String databaseName) throws SQLException 
	{
		initializeDatabaseConnection(databaseName);
	}
	
	/*Making in a way generic and WRONG way, just for the purpose of this class*/
	public void createIfNotExists(String sql) 
	{
		try {
			connection.createStatement().execute(sql);
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	private void initializeDatabaseConnection(String databaseName) throws SQLException 
	{
		var connectionString = "jdbc:sqlite:target/" + databaseName + ".db";
		connection = DriverManager.getConnection(connectionString);
	}

	public void Update(String statement, String ...parameters) throws SQLException 
	{
		var preparedStatement = prepareStatement(statement, parameters);		
		preparedStatement.execute();
	}

	public ResultSet ExecuteQuery(String query, String ...parameters) throws SQLException 
	{
		var preparedStatement = prepareStatement(query, parameters);
		return preparedStatement.executeQuery();
	}
	
	public void close() throws SQLException 
	{
		connection.close();
	}
	
	private PreparedStatement prepareStatement(String statement, String ...parameters) throws SQLException 
	{
		var preparedStatement = this.connection.prepareStatement(statement);
		
		for(int index = 0; index < parameters.length; index++)
			preparedStatement.setString(index + 1, parameters[index]);
		
		return preparedStatement;
	}

}
