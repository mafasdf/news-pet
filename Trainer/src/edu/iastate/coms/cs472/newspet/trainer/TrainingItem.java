package edu.iastate.coms.cs472.newspet.trainer;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Class to house the ID of a classifier to update, a document's text, and a desired category's ID.
 * @author Michael Fulker
 */
public class TrainingItem
{	
	private String documentText;
	//TODO: int or long?
	private long classifierID;
	private long categoryID;
	
	/**
	 * TODO:doc
	 * @param documentText
	 * @param classifierID
	 * @param categoryID
	 */
	public TrainingItem(String documentText, long classifierID, long categoryID)
	{
		this.documentText = documentText;
		this.classifierID = classifierID;
		this.categoryID = categoryID;
	}
	
	//TODO: needed?
	public TrainingItem(long feedItemID, long classifierID, long categoryID)
	{
		//this.documentText = get document by feedItemID;
		this.classifierID = classifierID;
		this.categoryID = categoryID;
		throw new NotImplementedException();
	}

	public String getDocumentText()
	{
		return documentText;
	}

	public long getClassifierID()
	{
		return classifierID;
	}

	public long getCategoryID()
	{
		return categoryID;
	}
}
