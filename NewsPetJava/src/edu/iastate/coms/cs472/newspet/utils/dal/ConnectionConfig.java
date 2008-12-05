package edu.iastate.coms.cs472.newspet.utils.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionConfig
{
	private static String sqliteDBPath;
	private static String user;
	private static String password;
	public static void setupParams(String sqliteDBPath, String user, String password)
	{
		ConnectionConfig.sqliteDBPath=sqliteDBPath;
		ConnectionConfig.user=user;
		ConnectionConfig.password=password;
	}
	
	
	static Connection createConnection()
	{
		if(sqliteDBPath==null || user == null || password == null)
			throw new RuntimeException("Must call setupParams before createConnection");
		
		try
		{
			//TODO: make configurable
			Class.forName("org.sqlite.JDBC").newInstance();
			return DriverManager.getConnection("jdbc:sqlite:"+sqliteDBPath, user, password);
		}
		catch(SQLException e)
		{
			throw new RuntimeException("SQLException while trying to establish a connection with database!", e);
		}
		catch(InstantiationException e)
		{
			throw new RuntimeException("InstantiationException: Could not instantiate jdbc driver!", e);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException("IllegalAccessException while trying to instantiate jdbc driver!", e);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException("ClassNotFoundException while tyring to instantiate jdbc driver!", e);
		}
	}
}
