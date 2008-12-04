package edu.iastate.coms.cs472.newspet.utils.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionConfig
{	
	static Connection createConnection()
	{
		try
		{
			//TODO: make configurable
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			return DriverManager.getConnection("jdbc:mysql://localhost/newspet", "root", "34eszx");
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not establish connection with database", e);
		}
		catch(InstantiationException e)
		{
			throw new RuntimeException("Could not instantiate jdbc driver", e);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException("Could not instantiate jdbc driver", e);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException("Could not instantiate jdbc driver", e);
		}
	}
}
