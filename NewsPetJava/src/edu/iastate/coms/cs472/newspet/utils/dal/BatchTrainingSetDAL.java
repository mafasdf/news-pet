package edu.iastate.coms.cs472.newspet.utils.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BatchTrainingSetDAL
{
	static final String TABLE_NAME = "feed_trainingset";
	
	static final String ID_COLUMN = "id";
	
	static final String PATH_COLUMN = "path";
	
	public static String getPath(int sourceId)
	{
		Connection conn = null;
		PreparedStatement getPath = null;
		ResultSet result = null;
		try
		{
			conn = ConnectionConfig.createConnection();
			String query = String.format("SELECT %s FROM %s WHERE %s=?;", PATH_COLUMN, TABLE_NAME, ID_COLUMN);
			getPath = conn.prepareStatement(query);
			getPath.setInt(1, sourceId);
			result = getPath.executeQuery();
			String toReturn = null;
			if(result.next())
			{
				toReturn = result.getString(PATH_COLUMN);
			}
			
			return toReturn;
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Can't retrieve trainingset:" + sourceId, e);
		}
		finally
		{
			if(result != null) try
			{
				result.close();
			}
			catch(SQLException e)
			{
				System.err.println("SQLException while trying to close a ResultSet!");
				System.err.println(e.getMessage());
			}
			try
			{
				if(getPath != null) getPath.close();
			}
			catch(SQLException e)
			{
				System.err.println("SQLException while trying to close a PreparedStatement!");
				System.err.println(e.getMessage());
			}
			try
			{
				if(conn != null)
				{
					if(!conn.getAutoCommit()) conn.commit();
					conn.close();
				}
			}
			catch(SQLException e)
			{
				System.err.println("SQLException while trying to close a Connection!");
				System.err.println(e.getMessage());
			}
		}
	}
}
