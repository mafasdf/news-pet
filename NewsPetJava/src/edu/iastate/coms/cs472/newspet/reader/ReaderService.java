package edu.iastate.coms.cs472.newspet.reader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import edu.iastate.coms.cs472.newspet.utils.Feed;
import edu.iastate.coms.cs472.newspet.utils.Pair;
import edu.iastate.coms.cs472.newspet.utils.dal.ConnectionConfig;
import edu.iastate.coms.cs472.newspet.utils.dal.FeedDAL;

public class ReaderService
{
	private static final long RSS_POLLING_PERIOD_MILLIS = 60000;//TODO: Is 1 minute for now.
	
	private ThreadPoolExecutor threadPool;
	
	public ReaderService()
	{
		//TODO: fine-tune / have configurable params
		//TODO: not one
		threadPool = new ThreadPoolExecutor(1, 1, 100, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	private void run()
	{
		long lastRunTime = 0;//will store UTC time in milliseconds
		
		while(true)
		{
			long currentTime = System.currentTimeMillis();
			//wait 
			long timeToWait = lastRunTime + RSS_POLLING_PERIOD_MILLIS - currentTime;
			if(timeToWait >= 0)
			{
				try
				{
					Thread.sleep(timeToWait);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			//get list of RSS feeds that need checking
			List<Feed> rssFeeds = FeedDAL.getFeedsOlderThan(new Date(currentTime + RSS_POLLING_PERIOD_MILLIS));
			
			//list to store retrieved channel data
			List<Pair<Feed, ChannelIF>> channelData = new ArrayList<Pair<Feed, ChannelIF>>(rssFeeds.size());
			//so we can block until all retrievals are complete
			List<Future<?>> futures = new ArrayList<Future<?>>(rssFeeds.size());
			
			for(Feed rssFeed : rssFeeds)
			{
				RSSRetrievalJob toRun = new RSSRetrievalJob(channelData, rssFeed);
				futures.add(threadPool.submit(toRun));
			}
			blockUntilDone(futures);
			futures.clear();
			
			//extract out downloaded feeditem information, and create (and submit) a classification job for each  
			for(Pair<Feed, ChannelIF> channelPair : channelData)
			{
				for(ItemIF item : channelPair.getB().getItems())
				{
					ItemClassificationJob toRun = new ItemClassificationJob(channelPair.getA(), item);
					futures.add(threadPool.submit(toRun));
				}
			}
			blockUntilDone(futures);
			
			lastRunTime = System.currentTimeMillis();
		}
	}
	
	private void blockUntilDone(List<Future<?>> futures)
	{
		for(Future<?> future : futures)
		{
			try
			{
				future.get();
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			catch(ExecutionException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args)
	{
		if(args.length != 3) throw new IllegalArgumentException("Requires parameters: DBPATH LOGIN PASSWORD");
		
		ConnectionConfig.setupParams(args[0], args[1], args[2]);
		
		(new ReaderService()).run();
	}
}
