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
public class CategoryDAL
{
	static final String TABLE_NAME = "feed_category";
	
	static final String ID_COLUMN = "id";
	
	static final String TRASH_COLUMN = "is_trash";
	
	static final String USERID_COLUMN = "owner_id";
	
	/**
	 * Retrieves the ID of the trash category for the given user, or null if no
	 * such category exists.
	 * 
	 * @param userID
	 * @return
	 */
	public static Integer getTrashCategoryIDForUser(int userID)
	{
		String query = "SELECT " + ID_COLUMN + " FROM " + TABLE_NAME + " WHERE " + USERID_COLUMN + "=? AND " + TRASH_COLUMN + "=? ;";
		
		Connection conn = null;
		PreparedStatement getTrashID = null;
		ResultSet result = null;
		try
		{
			conn = ConnectionConfig.createConnection();
			
			getTrashID = conn.prepareStatement(query);
			result = getTrashID.executeQuery();
			
			if(result.next()) return result.getInt(ID_COLUMN);
			return null;
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not retrieve trash category for user:" + userID, e);
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
				if(getTrashID != null) getTrashID.close();
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
