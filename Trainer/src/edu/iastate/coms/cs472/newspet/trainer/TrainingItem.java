package edu.iastate.coms.cs472.newspet.trainer;

import cc.mallet.types.Instance;

public class TrainingItem
{	
	private int classifierID;
	private Instance data;
	public int getClassifierID()
	{
		return classifierID;
	}
	public Instance getData()
	{
		return data;
	}
	
	
	public TrainingItem(int classifierID, Instance data)
	{
		this.classifierID = classifierID;
		this.data = data;
	}
	
	public TrainingItem(int classifierID, int targetCategory, String documentData)
	{
		this(classifierID, new Instance(documentData, targetCategory, null,null));
	}
}
