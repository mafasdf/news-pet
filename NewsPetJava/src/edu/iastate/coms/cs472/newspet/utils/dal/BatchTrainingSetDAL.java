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
		try
		{
			Connection conn = ConnectionConfig.createConnection();
			String query = String.format("SELECT %s FROM %s WHERE %s=?;", PATH_COLUMN, TABLE_NAME, ID_COLUMN);
			PreparedStatement getPath = conn.prepareCall(query);
			getPath.setInt(1, sourceId);
			ResultSet result = getPath.executeQuery();
			String toReturn = null;
			if(result.next())
			{
				toReturn = result.getString(PATH_COLUMN);
			}
			result.close();
			getPath.close();
			if(!conn.getAutoCommit()) conn.commit();
			conn.close();
			return toReturn;
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Can't retrieve trainingset:" + sourceId, e);
		}
	}
}
