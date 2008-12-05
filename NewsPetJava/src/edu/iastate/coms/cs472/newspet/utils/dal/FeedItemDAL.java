package edu.iastate.coms.cs472.newspet.utils.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.iastate.coms.cs472.newspet.utils.Pair;

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
	
	private static final String URL_EXISTS_QUERY = String.format(
			"SELECT EXISTS ( SELECT * FROM %s F INNER JOIN %s C ON F.%s=C.%s WHERE F.%s=? AND C.%s=?);", TABLE_NAME, CategoryDAL.TABLE_NAME,
			CATEGORYID_COLUMN, CategoryDAL.ID_COLUMN, URL_COLUMN, CategoryDAL.USERID_COLUMN);
	
	public static boolean existsURL(String url, int userID)
	{
		boolean toReturn;
		Connection conn = ConnectionConfig.createConnection();
		
		try
		{
			PreparedStatement checkExists = conn.prepareStatement(URL_EXISTS_QUERY);
			ResultSet booleanResult = checkExists.executeQuery();
			checkExists.setString(1, url);
			checkExists.setInt(2, userID);
			
			booleanResult.next();
			
			toReturn = booleanResult.getBoolean(0);
			booleanResult.close();
			checkExists.close();
			
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not check for existence of FeedItem url", e);
		}
		
		return toReturn;
	}
	
	/**
	 * Saves a new feedItem, aborting if item with the same user & url already exists.
	 */
	public static void saveNewFeedItem(String title, String creator, String description, String url, int categoryId, int feedId, int userId)
	{
		Connection conn = ConnectionConfig.createConnection();		
		
		try
		{	
			//TODO: more elegant locking
			//check for existence of a feeditem
			PreparedStatement checkExists = conn.prepareStatement(URL_EXISTS_QUERY);
			checkExists.setString(1, url);
			checkExists.setInt(2, userId);
			ResultSet booleanResult = checkExists.executeQuery();
			booleanResult.next();
			boolean alreadyExists = booleanResult.getBoolean(0);
			booleanResult.close();
			checkExists.close();
			//only insert if item with same url doesn't exist
			if(!alreadyExists)
			{
				PreparedStatement insert = conn.prepareStatement(String.format("INSERT INTO %s (%s, %s, %s, %s) values (?, ?, ?, ?, ?);"/**/));
				//TODO
			}
			
			//TODO: more closing
			if(!conn.getAutoCommit())
				conn.commit();
			conn.close();
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not insert feedItem:"+url, e);
		}
	}
}
