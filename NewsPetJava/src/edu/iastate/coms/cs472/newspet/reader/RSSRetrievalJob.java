package edu.iastate.coms.cs472.newspet.reader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;
import edu.iastate.coms.cs472.newspet.utils.Feed;
import edu.iastate.coms.cs472.newspet.utils.dal.FeedDAL;

/**
 * Job that retrieves
 * 
 * @author Michael Fulker
 */
public class RSSRetrievalJob implements Runnable
{
	/**
	 * Feed to read from.
	 */
	private Feed feed;
	
	private ReaderService readerService; 
	
	/**
	 * Creates a new RSS Retrieval job.
	 * @param feed
	 *        Feed to read from.
	 * @param readerService TODO
	 */
	public RSSRetrievalJob(Feed feed, ReaderService readerService)
	{
		this.feed = feed;
		this.readerService = readerService;
	}
	
	/**
	 * Retrieves channel data and stores in resultStorage.
	 */
	public void run()
	{
		URL url;
		ChannelIF channel;
		try
		{
			url = new URL(feed.getUrl());
		}
		catch(MalformedURLException e)
		{
			System.err.println(e.getClass().getName());
			e.printStackTrace();
			return;
		}
		try
		{
			channel = FeedParser.parse(new ChannelBuilder(), url);
		}
		catch(IOException e)
		{
			System.err.println(e.getClass().getName());
			e.printStackTrace();
			return;
		}
		catch(ParseException e)
		{
			System.err.println(e.getClass().getName());
			e.printStackTrace();
			return;
		}
		
		readerService.addToRSSResultProcessingThreadPool(feed, channel);
		FeedDAL.updateCrawlTime(new Date(System.currentTimeMillis()), feed.getId());
	}
}
