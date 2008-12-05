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
	static final String ID_COLUMN = "id";
	static final String URL_COLUMN = "link";
	static final String CATEGORYID_COLUMN = "category_id";
	static final String DATE_COLUMN = null;
	static final String TITLE_COLUMN = null;
	static final String AUTHOR_COLUMN = null;
	static final String BODY_COLUMN = null;
	static final String OPINION_COLUMN = null;
	static final String WASVIEWED_COLUMN = null;
	
	//used in multiple methods
	static final String URL_EXISTS_QUERY = String.format(
			"SELECT EXISTS ( SELECT * FROM %s F INNER JOIN %s C ON F.%s=C.%s WHERE F.%s=? AND C.%s=?);", TABLE_NAME, CategoryDAL.TABLE_NAME,
			CATEGORYID_COLUMN, CategoryDAL.ID_COLUMN, URL_COLUMN, CategoryDAL.USERID_COLUMN);
	private static final Object FEEDID_COLUMN = null;
	
	
	
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
	 * Saves a new feedItem, aborting if item with the same user & url already
	 * exists.
	 */
	public static void saveNewFeedItem(String title, String creator, String description, String url, int categoryId, int feedId, int userId)
	{
		Connection conn = ConnectionConfig.createConnection();		
		
		try
		{
			//use a transaction to prevent multiples of the same feed item being added
			conn.setAutoCommit(false);
			
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
				PreparedStatement insert = conn.prepareStatement(String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s) values (?, ?, ?, ?, ?, ?, ?, ?, ?);", TABLE_NAME, 
						DATE_COLUMN, TITLE_COLUMN, AUTHOR_COLUMN, BODY_COLUMN, 
						URL_COLUMN, OPINION_COLUMN, CATEGORYID_COLUMN, FEEDID_COLUMN, WASVIEWED_COLUMN));
				insert.setDate(1, new java.sql.Date(System.currentTimeMillis()));
				insert.setString(2, title == null ? "" : title);
				insert.setString(3, creator == null ? "" : creator);
				insert.setString(4, description== null ? "" : description);
				insert.setString(5, url== null ? "" : url);
				insert.setInt(6, 0);//default = 0
				insert.setInt(7, categoryId);
				insert.setInt(8, feedId);
				insert.setBoolean(9, false);
				
				insert.executeUpdate();
				insert.close();
			}
			
			
			
			//commit transaction
			conn.commit();
			
			
			conn.close();
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not insert feedItem:"+url, e);
		}
	}
	
	public static String getFeedItemText(int feedItemID)
	{
		try
		{
			Connection conn = ConnectionConfig.createConnection();
			
			String query = String.format("SELECT %s,%s,%s  FROM %s WHERE %s=?", AUTHOR_COLUMN, TITLE_COLUMN, BODY_COLUMN, TABLE_NAME, ID_COLUMN);
			PreparedStatement getFeedItem = conn.prepareStatement(query);
			ResultSet result = getFeedItem.executeQuery();
			StringBuilder toReturn = new StringBuilder();
			if(result.next())
			{
				toReturn.append(result.getString(AUTHOR_COLUMN));
				toReturn.append(" ");
				toReturn.append(result.getString(TITLE_COLUMN));
				toReturn.append(" ");
				toReturn.append(result.getString(BODY_COLUMN));
			}
			
			result.close();
			getFeedItem.close();
			if(!conn.getAutoCommit())
				conn.commit();
			conn.close();
			return toReturn.toString();
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not retreive feedItem: "+feedItemID, e);
		}
		
	}
}
