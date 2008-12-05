package edu.iastate.coms.cs472.newspet.utils.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data access layer for FeedItems.
 * 
 * @author Michael Fulker
 */
public class FeedItemDAL
{
	static final String TABLE_NAME = "feed_feeditem";
	static final String URL_COLUMN = "link";
	static final String CATEGORYID_COLUMN = "category_id";
	
	public static boolean existsURL(String url, int userID)
	{
		boolean toReturn;
		Connection conn = ConnectionConfig.createConnection();
		
		String query = String.format("SELECT EXISTS ( SELECT * FROM %s F INNER JOIN %s C ON F.%s=C.%s WHERE F.%s=? AND C.%s=?);", TABLE_NAME,
				CategoryDAL.TABLE_NAME, CATEGORYID_COLUMN, CategoryDAL.ID_COLUMN, URL_COLUMN, CategoryDAL.USERID_COLUMN);
		
		try
		{
			PreparedStatement checkExists = conn.prepareStatement(query);
			ResultSet booleanResult = checkExists.executeQuery();
			
			booleanResult.next();
			
			toReturn = booleanResult.getBoolean(0);
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not check for existence of FeedItem url", e);
		}
		
		return toReturn;
	}
}
