package edu.iastate.coms.cs472.newspet.reader;

import de.nava.informa.core.ChannelIF;
import edu.iastate.coms.cs472.newspet.utils.Feed;

public class FeedChannelPair
{	
	private Feed feed;
	private ChannelIF channel;
	/**
	 * @param feed
	 * @param channel
	 */
	public FeedChannelPair(Feed feed, ChannelIF channel)
	{
		this.feed = feed;
		this.channel = channel;
	}
	public Feed getFeed()
	{
		return feed;
	}
	public ChannelIF getChannel()
	{
		return channel;
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		result = prime * result + ((feed == null) ? 0 : feed.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		final FeedChannelPair other = (FeedChannelPair) obj;
		if(channel == null)
		{
			if(other.channel != null) return false;
		}
		else if(!channel.equals(other.channel)) return false;
		if(feed == null)
		{
			if(other.feed != null) return false;
		}
		else if(!feed.equals(other.feed)) return false;
		return true;
	}
	
	
}
