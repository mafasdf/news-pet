package edu.iastate.coms.cs472.newspet.reader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ItemIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;
import edu.iastate.coms.cs472.newspet.utils.Feed;
import edu.iastate.coms.cs472.newspet.utils.dal.FeedDAL;

public class ReaderService
{
	private static final long RSS_POLLING_PERIOD_MILLIS = 60000;//TODO: Have be 1 minute for now.
	
	private ThreadPoolExecutor threadPool;
	
	public ReaderService()
	{
		//TODO: fine-tune / have configurable params
		threadPool = new ThreadPoolExecutor(32, 32, 100, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		(new ReaderService()).run();
		
		//TODO
		/*Set<ItemIF> items = channel.getItems();
		
		ItemIF i = items.iterator().next();*/
		
		/*
		 * ChannelIF c= channel; c.addCategory(null); c.addItem(null);
		 * c.addObserver(null); c.equals(null); c.getAttributeValue(null, null);
		 * c.getAttributeValues(null, null); c.getCategories(); c.getCloud();
		 * c.getCopyright(); c.getCreator(); c.getDescription(); c.getDocs();
		 * c.getElementValue(null); c.getElementValues(null, null);
		 * c.getFormat(); c.getGenerator(); c.getId(); c.getImage();
		 * c.getItem(0); c.getItems(); c.getLastBuildDate(); c.getLastUpdated();
		 * c.getLastUpdated(); c.getLocation(); c.getRating(); c.getSite();
		 * c.getTextInput();
		 */
	}
	
	private void run()
	{
		while(true)
		{
			Date lastRunTime = new Date(System.currentTimeMillis());
			
			//get list of RSS feeds that need checking
			List<Feed> rssFeeds = FeedDAL.getFeedsOlderThan(new Date(System.currentTimeMillis() + RSS_POLLING_PERIOD_MILLIS));
			
			//list to store retrieved data
			List<FeedChannelPair> channelData = new ArrayList<FeedChannelPair>(rssFeeds.size());
			//so we can block until all retrievals are complete
			List<Future<Object>> futures = new ArrayList<Future<Object>>(rssFeeds.size());
			
			for(Feed rssFeed: rssFeeds)
			{
				RSSRetrievalJob toRun = new RSSRetrievalJob(channelData, rssFeed);
				threadPool.submit(toRun);
			}
			//block until done
			for(Future future:futures)
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
			
			//TODO
			
		}
	}
	
}
