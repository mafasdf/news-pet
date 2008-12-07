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
	
	static final String ID_COLUMN = "id";
	
	static final String URL_COLUMN = "link";
	
	static final String CATEGORYID_COLUMN = "category_id";
	
	static final String TITLE_COLUMN = "title";
	
	static final String AUTHOR_COLUMN = "author";
	
	static final String BODY_COLUMN = "body";
	
	static final String OPINION_COLUMN = "opinion";
	static final int DEFAULT_OPINION_VALUE = 0;
	
	static final String WASVIEWED_COLUMN = "was_viewed";
	static final boolean DEFAULT_WASVIEWED_VALUE = false;
	
	//used in multiple methods
	static final String URL_EXISTS_QUERY = String.format("SELECT EXISTS ( SELECT * FROM %s F INNER JOIN %s C ON F.%s=C.%s WHERE F.%s=? AND C.%s=?);",
			TABLE_NAME, CategoryDAL.TABLE_NAME, CATEGORYID_COLUMN, CategoryDAL.ID_COLUMN, URL_COLUMN, CategoryDAL.USERID_COLUMN);
	
	private static final Object FEEDID_COLUMN = "feed_id";
	
	public static boolean existsURL(String url, int userID)
	{
		Connection conn = ConnectionConfig.createConnection();
		
		PreparedStatement checkExists = null;
		ResultSet booleanResult = null;
		try
		{
			checkExists = conn.prepareStatement(URL_EXISTS_QUERY);
			booleanResult = checkExists.executeQuery();
			checkExists.setString(1, url);
			checkExists.setInt(2, userID);
			
			booleanResult.next();
			
			return booleanResult.getBoolean(1);
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not check for existence of FeedItem url", e);
		}
		finally
		{
			if(booleanResult != null) try
			{
				booleanResult.close();
			}
			catch(SQLException e)
			{
				System.err.println("SQLException while trying to close a ResultSet!");
				System.err.println(e.getMessage());
			}
			try
			{
				if(checkExists != null) checkExists.close();
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
	
	/**
	 * Saves a new feedItem, aborting if item with the same user & url already
	 * exists.
	 */
	public static void saveNewFeedItem(String title, String creator, String description, String url, int categoryId, int feedId, int userId)
	{
		Connection conn = ConnectionConfig.createConnection();
		
		PreparedStatement checkExists = null;
		ResultSet booleanResult = null;
		boolean alreadyExists;
		try
		{
			//use a transaction to prevent multiples of the same feed item being added
			conn.setAutoCommit(false);
			
			//check for existence of a feeditem
			checkExists = conn.prepareStatement(URL_EXISTS_QUERY);
			checkExists.setString(1, url);
			checkExists.setInt(2, userId);
			booleanResult = checkExists.executeQuery();
			booleanResult.next();
			alreadyExists = booleanResult.getBoolean(1);
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not insert feedItem:" + url, e);
		}
		finally
		{
			if(booleanResult != null) try
			{
				booleanResult.close();
			}
			catch(SQLException e)
			{
				System.err.println("SQLException while trying to close a ResultSet!");
				System.err.println(e.getMessage());
			}
			try
			{
				if(checkExists != null) checkExists.close();
			}
			catch(SQLException e)
			{
				System.err.println("SQLException while trying to close a PreparedStatement!");
				System.err.println(e.getMessage());
			}
		}
		
		PreparedStatement insert = null;
		try
		{
			//only insert if item with same url doesn't exist
			if(!alreadyExists)
			{
				insert = conn.prepareStatement(String.format(
						"INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) values (?, ?, ?, ?, ?, ?, ?, ?);", TABLE_NAME,
						TITLE_COLUMN, AUTHOR_COLUMN, BODY_COLUMN, URL_COLUMN, OPINION_COLUMN, CATEGORYID_COLUMN, FEEDID_COLUMN, WASVIEWED_COLUMN));
				insert.setString(1, title == null ? "" : title);
				insert.setString(2, creator == null ? "" : creator);
				insert.setString(3, description == null ? "" : description);
				insert.setString(4, url == null ? "" : url);
				insert.setInt(5, DEFAULT_OPINION_VALUE);
				insert.setInt(6, categoryId);
				insert.setInt(7, feedId);
				insert.setBoolean(8, DEFAULT_WASVIEWED_VALUE);
				
				insert.executeUpdate();
			}
			
			//commit transaction
			conn.commit();
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not insert feedItem:" + url, e);
		}
		finally
		{
			try
			{
				if(insert != null) insert.close();
			}
			catch(SQLException e)
			{
				System.err.println("SQLException while trying to close a PreparedStatement!");
				System.err.println(e.getMessage());
			}
			try
			{
				conn.close();
			}
			catch(SQLException e)
			{
				System.err.println("SQLException while trying to close a Connection!");
				System.err.println(e.getMessage());
			}
		}
	}
	
	public static String getFeedItemText(int feedItemID)
	{
		Connection conn = ConnectionConfig.createConnection();
		String query = String.format("SELECT %s,%s,%s  FROM %s WHERE %s=?", AUTHOR_COLUMN, TITLE_COLUMN, BODY_COLUMN, TABLE_NAME, ID_COLUMN);
		
		PreparedStatement getFeedItem = null;
		ResultSet result = null;
		try
		{
			getFeedItem = conn.prepareStatement(query);
			getFeedItem.setInt(1, feedItemID);
			result = getFeedItem.executeQuery();
			StringBuilder toReturn = new StringBuilder();
			if(result.next())//TODO
			{
				toReturn.append(result.getString(AUTHOR_COLUMN));
				toReturn.append(" ");
				toReturn.append(result.getString(TITLE_COLUMN));
				toReturn.append(" ");
				toReturn.append(result.getString(BODY_COLUMN));
			}
			
			return toReturn.toString();
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not retreive feedItem: " + feedItemID, e);
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
				if(getFeedItem != null) getFeedItem.close();
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
