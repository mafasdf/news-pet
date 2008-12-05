package edu.iastate.coms.cs472.newspet.reader;

import java.util.ArrayList;
import java.util.List;

import cc.mallet.classify.Classifier;
import cc.mallet.types.Label;
import cc.mallet.types.LabelVector;
import de.nava.informa.core.ItemIF;
import edu.iastate.coms.cs472.newspet.utils.Feed;
import edu.iastate.coms.cs472.newspet.utils.Pair;
import edu.iastate.coms.cs472.newspet.utils.dal.CategoryDAL;
import edu.iastate.coms.cs472.newspet.utils.dal.DatabaseAccessLayer;
import edu.iastate.coms.cs472.newspet.utils.dal.FeedItemDAL;

public class ItemClassificationJob implements Runnable
{
	private static final double PROBABILITY_SIGNIFICANCE_RATIO = 1.2;
	
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
		//check if this item already exists for this user (using URL as an equivalence comparison), and abort if so
		if(FeedItemDAL.existsURL(feedItem.getLink().toString(), feed.getUserId())) return;
		
		Classifier classifier = DatabaseAccessLayer.getClassifier(feed.getUserId());
		int trashCategory = CategoryDAL.getTrashCategoryIDForUser(feed.getUserId());
		
		//extract out all textual information
		StringBuilder text = new StringBuilder();
		if(feedItem.getCreator() != null)
		{
			text.append(feedItem.getCreator());
			text.append(" ");
		}
		if(feedItem.getDescription() != null)
		{
			text.append(feedItem.getDescription());
			text.append(" ");
		}
		if(feedItem.getSubject() != null)
		{
			text.append(feedItem.getSubject());
			text.append(" ");
		}
		if(feedItem.getTitle() != null)
		{
			text.append(feedItem.getTitle());
			text.append(" ");
		}
		
		//strip out any remnant html tags (img tags in description, etc)
		String bareText = text.toString().replaceAll("<[^<>]*>", " ");
		
		LabelVector classificationResultVector = classifier.classify(bareText).getLabelVector();
		List<Pair<Integer, Double>> categoryProbabilities = getClassificationValues(classificationResultVector);
		
		//get maximum probability category
		Pair<Integer, Double> bestCategory = findBestCategory(categoryProbabilities);
		
		int bestCategoryID;
		//check if probability scaled by the number of categories is at least some cutoff value
		//(If the probability is too close to 1/numCategories, trash is the chosen category)  
		if(bestCategory.getB().doubleValue() * categoryProbabilities.size() > PROBABILITY_SIGNIFICANCE_RATIO)
		{
			bestCategoryID = bestCategory.getA().intValue();
		}
		else
		{
			bestCategoryID = trashCategory;
		}
		
		//persist a new FeedItem in the proper category
		FeedItemDAL.saveNewFeedItem(feedItem.getTitle(), feedItem.getCreator(), feedItem.getDescription(), feedItem.getLink().toString(), bestCategoryID, feed.getId(), feed.getUserId());
	}
	
	public static List<Pair<Integer, Double>> getClassificationValues(LabelVector classificationResultVector)
	{
		double[] probabilities = classificationResultVector.getValues();
		
		List<Pair<Integer, Double>> toReturn = new ArrayList<Pair<Integer, Double>>(probabilities.length);
		
		for(int i = 0; i < probabilities.length; i++)
		{
			Label label = classificationResultVector.labelAtLocation(i);
			Integer categoryID = (Integer) label.getEntry();
			Pair<Integer, Double> toAdd = new Pair<Integer, Double>(categoryID, Double.valueOf(probabilities[i]));
			toReturn.add(toAdd);
		}
		
		return toReturn;
	}
	
	private Pair<Integer, Double> findBestCategory(List<Pair<Integer, Double>> categoryProbabilities)
	{
		Pair<Integer, Double> best = null;
		for(Pair<Integer, Double> candidate : categoryProbabilities)
		{
			if(best == null || candidate.getB() > best.getB()) best = candidate;
		}
		return best;
	}
}
