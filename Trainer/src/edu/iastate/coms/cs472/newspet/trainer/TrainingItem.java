package edu.iastate.coms.cs472.newspet.trainer;

import cc.mallet.types.Instance;

public class TrainingItem
{	
	private long classifierID;
	private Instance data;
	public long getClassifierID()
	{
		return classifierID;
	}
	public Instance getData()
	{
		return data;
	}
	
	
	public TrainingItem(long classifierID, Instance data)
	{
		this.classifierID = classifierID;
		this.data = data;
	}
	
	public TrainingItem(long classifierID, long targetCategory, String documentData)
	{
		this(classifierID, new Instance(documentData, targetCategory, null,null));
	}
}
