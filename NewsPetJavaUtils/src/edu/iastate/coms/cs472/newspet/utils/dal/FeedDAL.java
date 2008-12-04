package edu.iastate.coms.cs472.newspet.utils.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import edu.iastate.coms.cs472.newspet.utils.Feed;

/**
 * Data access layer for Classifier objects.
 * 
 * @author Michael Fulker
 */
public class FeedDAL
{
	private static final String ID_COLUMN = "id";
	private static final String URL_COLUMN = "url";
	private static final String USERID_COLUMN = "subscriber_id";
	private static final String LASTCRAWLED_COLUMN = "last_crawled";
	private static final String FEED_TABLE = "feed_feeditem";
	
	/**
	 * Gets feed objects with "lastCrawled" timestamps older than the given cutoff time. 
	 * @param cutoff Cutoff time in UTC form.
	 * @return
	 */
	public static List<Feed> getFeedsOlderThan(java.util.Date cutoff)
	{
		List<Feed> toReturn = new ArrayList<Feed>();
		
		
		Connection conn = ConnectionConfig.createConnection();
		
		String query = "SELECT " + ID_COLUMN + " " + URL_COLUMN + " " + USERID_COLUMN + " " + LASTCRAWLED_COLUMN + " FROM " + FEED_TABLE + " WHERE "
				+ LASTCRAWLED_COLUMN + " < ? ORDER BY " + LASTCRAWLED_COLUMN + " ASC;";
		try
		{
			PreparedStatement getFeeds = conn.prepareStatement(query);
			long currentDateMillis = System.currentTimeMillis();
			getFeeds.setDate(1, new java.sql.Date(currentDateMillis));
			ResultSet feedResults = getFeeds.executeQuery();
			
			while(feedResults.next())
			{
				int feedID = feedResults.getInt(ID_COLUMN);
				String url = feedResults.getString(URL_COLUMN);
				int userID = feedResults.getInt(USERID_COLUMN);
				java.util.Date lastCrawled = feedResults.getDate(LASTCRAWLED_COLUMN);
				
				Feed toAdd = new Feed(feedID, url, userID, lastCrawled);
				toReturn.add(toAdd);
			}
			
			feedResults.close();
			getFeeds.close();
			conn.close();
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not retrieve Feed records", e);
		}
		
		return toReturn;
	}
}
