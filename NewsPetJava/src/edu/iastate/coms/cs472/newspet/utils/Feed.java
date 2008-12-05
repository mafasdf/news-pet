package edu.iastate.coms.cs472.newspet.utils;

import java.util.Date;

/**
 * Class to represent a feed retrieved from the database.
 * 
 * @author Michael Fulker
 */
public class Feed
{
	private int id;
	
	private String url;
	
	private int userId;
	
	private Date lastCrawled;
	
	public Feed(int id, String url, int userId, Date lastCrawled)
	{
		this.id = id;
		this.url = url;
		this.userId = userId;
		this.lastCrawled = lastCrawled;
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public int getUserId()
	{
		return userId;
	}
	
	public Date getLastCrawled()
	{
		return lastCrawled;
	}
}
