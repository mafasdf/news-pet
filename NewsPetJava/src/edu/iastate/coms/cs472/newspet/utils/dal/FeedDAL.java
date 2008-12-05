package edu.iastate.coms.cs472.newspet.utils.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.iastate.coms.cs472.newspet.utils.Feed;

/**
 * Data access layer for Classifier objects.
 * 
 * @author Michael Fulker
 */
public class FeedDAL
{
	static final String ID_COLUMN = "id";
	
	static final String URL_COLUMN = "url";
	
	static final String USERID_COLUMN = "subscriber_id";
	
	static final String LASTCRAWLED_COLUMN = "last_crawled";
	
	static final String FEED_TABLE = "feed_feed";
	
	/**
	 * Gets feed objects with "lastCrawled" timestamps older than the given
	 * cutoff time.
	 * 
	 * @param cutoff
	 *        Cutoff time in UTC form.
	 * @return
	 */
	public static List<Feed> getFeedsOlderThan(java.util.Date cutoff)
	{
		Connection conn = ConnectionConfig.createConnection();
		//TODO: remove 1=1
		String query = "SELECT " + ID_COLUMN + ", " + URL_COLUMN + ", " + USERID_COLUMN + ", " + LASTCRAWLED_COLUMN + " FROM " + FEED_TABLE + " WHERE " + LASTCRAWLED_COLUMN + " < ? OR 1=1 ORDER BY " + LASTCRAWLED_COLUMN + " ASC;";
		
		PreparedStatement getFeeds = null;
		ResultSet feedResults = null;
		try
		{
			getFeeds = conn.prepareStatement(query);
			getFeeds.setDate(1, new java.sql.Date(cutoff.getTime()));
			feedResults = getFeeds.executeQuery();
			
			List<Feed> toReturn = new ArrayList<Feed>();
			while(feedResults.next())
			{
				int feedID = feedResults.getInt(ID_COLUMN);
				String url = feedResults.getString(URL_COLUMN);
				int userID = feedResults.getInt(USERID_COLUMN);
				java.util.Date lastCrawled = feedResults.getDate(LASTCRAWLED_COLUMN);
				
				Feed toAdd = new Feed(feedID, url, userID, lastCrawled);
				toReturn.add(toAdd);
			}
			
			return toReturn;
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not retrieve Feed records", e);
		}
		finally
		{
			if(feedResults != null) try
			{
				feedResults.close();
			}
			catch(SQLException e)
			{
				System.err.println("SQLException while trying to close a ResultSet!");
				System.err.println(e.getMessage());
			}
			try
			{
				if(getFeeds != null) getFeeds.close();
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
