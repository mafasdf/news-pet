package edu.iastate.coms.cs472.newspet.reader;
import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.NaiveBayes;
import de.nava.informa.core.ItemIF;
import edu.iastate.coms.cs472.newspet.utils.Feed;
import edu.iastate.coms.cs472.newspet.utils.dal.CategoryDAL;
import edu.iastate.coms.cs472.newspet.utils.dal.ClassifierDAL;
import edu.iastate.coms.cs472.newspet.utils.dal.FeedItemDAL;


public class ItemClassificationJob implements Runnable
{
	/**
	 * The feed associated with the feed item to classify.
	 */
	private Feed feed;
	
	/**
	 * The item to classify and persist.
	 */
	private ItemIF feedItem;
	
	public ItemClassificationJob(Feed feed, ItemIF feedItem)
	{
		this.feed = feed;
		this.feedItem = feedItem;
	}




	/**
	 * <p>
	 * Classifies the given feed item and persists it with the corresponding
	 * CategoryID.
	 * </p>
	 * <p>
	 * If the feedItem url already exists, this method will abort.
	 * </p>
	 */
	public void run()
	{
		//check if this item already exists (using URL as an equivalence comparison), and abort if so
		if(FeedItemDAL.existsURL(feedItem.getLink().toString(), feed.getUserId()))
			return;
		
		Classifier classifier = ClassifierDAL.getClassifier(feed.getUserId());
		int trashCategory = CategoryDAL.getTrashCategoryIDForUser(feed.getUserId());
		
		//extract out all textual information
		StringBuilder text = new StringBuilder();
		if(feedItem.getCreator()!=null)
		{
			text.append(feedItem.getCreator());
			text.append(" ");
		}
		if(feedItem.getDescription()!=null)
		{
			text.append(feedItem.getDescription());
			text.append(" ");
		}
		if(feedItem.getSubject()!=null)
		{
			text.append(feedItem.getSubject());
			text.append(" ");
		}
		if(feedItem.getTitle()!=null)
		{
			text.append(feedItem.getTitle());
			text.append(" ");
		}
		
		//strip out any remnant html tags (img tags in description, etc)
		String bareText = text.toString().replaceAll("<[^<>]*>", " ");
		
		Classification classificationResult = classifier.classify(bareText);
		
	}
	
}
