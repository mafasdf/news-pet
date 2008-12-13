package edu.iastate.coms.cs472.newspet.reader;

import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import edu.iastate.coms.cs472.newspet.utils.Feed;
import edu.iastate.coms.cs472.newspet.utils.dal.ConnectionConfig;
import edu.iastate.coms.cs472.newspet.utils.dal.FeedDAL;

public class ReaderService
{
	private long rssPollingPeriodMS;
	
	private ThreadPoolExecutor readRSSThreadPool;
	
	private ThreadPoolExecutor processRSSResultThreadPool;
	
	public ReaderService()
	{
		this(60000, 0, 10, 30, TimeUnit.SECONDS);
	}
	
	public ReaderService(long rssPollingPeriodMS, int corePoolSize, int maximumPoolSize, int keepAliveTime, TimeUnit unit)
	{
		this.rssPollingPeriodMS = rssPollingPeriodMS;
		readRSSThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<Runnable>());
		processRSSResultThreadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<Runnable>());
	}
	
	private void run()
	{
		long lastRunTime = 0;//will store UTC time in milliseconds
		
		while(true)
		{
			long currentTime = System.currentTimeMillis();
			//wait 
			long timeToWait = lastRunTime + rssPollingPeriodMS - currentTime;
			if(timeToWait >= 0)
			{
				try
				{
					Thread.sleep(timeToWait);
				}
				catch(InterruptedException e)
				{
					System.err.println("Interrupted while waiting (for " + timeToWait + " ms) to update news feeds.");
				}
			}
			
			//get list of RSS feeds that need checking
			List<Feed> rssFeeds = FeedDAL.getFeedsOlderThan(new Date(currentTime + rssPollingPeriodMS));
			
			for(Feed rssFeed : rssFeeds)
			{
				RSSRetrievalJob toRun = new RSSRetrievalJob(rssFeed, this);
				readRSSThreadPool.submit(toRun);
			}
			
			lastRunTime = System.currentTimeMillis();
		}
	}
	
	public void addToRSSResultProcessingThreadPool(Feed feed, ChannelIF channel)
	{
		for(ItemIF item : channel.getItems())
		{
			ItemClassificationJob toRun = new ItemClassificationJob(feed, item);
			processRSSResultThreadPool.submit(toRun);
		}
	}
	
	public static void main(String[] args)
	{
		if(args.length != 3) throw new IllegalArgumentException("Requires parameters: DBPATH LOGIN PASSWORD");
		
		ConnectionConfig.setupParams(args[0], args[1], args[2]);
		
		(new ReaderService()).run();
	}
}
