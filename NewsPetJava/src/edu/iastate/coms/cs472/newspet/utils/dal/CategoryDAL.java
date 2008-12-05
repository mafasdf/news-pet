package edu.iastate.coms.cs472.newspet.utils.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
	 * Retrieves the ID of the trash category for the given user, or null if no such category exists.
	 * @param userID
	 * @return
	 */
	public static Integer getTrashCategoryIDForUser(int userID)
	{
		Connection conn = ConnectionConfig.createConnection();
		Integer toReturn;
		
		String query = "SELECT " + ID_COLUMN + " FROM " + TABLE_NAME + " WHERE " + USERID_COLUMN + "=? AND " + TRASH_COLUMN + "=? ;";
		
		PreparedStatement getTrashID;
		try
		{
			getTrashID = conn.prepareStatement(query);
			java.sql.ResultSet result = getTrashID.executeQuery();
			
			if(!result.next())
				toReturn = null;
			else
				toReturn = result.getInt(ID_COLUMN);
			
			result.close();
			getTrashID.close();
			//TODO: check this in other DALs
			if(!conn.getAutoCommit())
				conn.commit();
			conn.close();
		}
		catch(SQLException e)
		{
			throw new RuntimeException("Could not retrieve trash category for user:"+userID, e);
		}
		
		return toReturn;
	}
}
