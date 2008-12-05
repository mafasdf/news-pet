package edu.iastate.coms.cs472.newspet.reader;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import de.nava.informa.core.ChannelIF;
import de.nava.informa.core.ParseException;
import de.nava.informa.impl.basic.ChannelBuilder;
import de.nava.informa.parsers.FeedParser;
import edu.iastate.coms.cs472.newspet.utils.Feed;
import edu.iastate.coms.cs472.newspet.utils.Pair;


/**
 * Job that retrieves 
 * @author Michael Fulker
 *
 */
public class RSSRetrievalJob implements Runnable
{
	/**
	 * Shared list of results.
	 * Must be added to in a synchronized manner.
	 */
	private Collection<Pair<Feed,ChannelIF>> resultStorage;
	
	/**
	 * Feed to read from.
	 */
	private Feed feed;
	
	
	
	
	/**
	 * Creates a new RSS Retrieval job.
	 * @param resultStorage
	 * The location to store retrieved channel data.
	 * @param feed
	 * Feed to read from.
	 */
	public RSSRetrievalJob(Collection<Pair<Feed,ChannelIF>> resultStorage, Feed feed)
	{
		this.resultStorage = resultStorage;
		this.feed = feed;
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
			url =  new URL(feed.getUrl());
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
			return;
		}
		try
		{
			channel = FeedParser.parse(new ChannelBuilder(), url);
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
			return;
		}
		catch(ParseException e1)
		{
			e1.printStackTrace();
			return;
		}
		
		Pair<Feed,ChannelIF> toAdd = new Pair<Feed,ChannelIF>(feed, channel);
		synchronized(resultStorage)
		{
			resultStorage.add(toAdd);
		}
	}
	
}
