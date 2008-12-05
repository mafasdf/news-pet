package edu.iastate.coms.cs472.newspet.trainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;
import edu.iastate.coms.cs472.newspet.utils.DocumentConversion;
import edu.iastate.coms.cs472.newspet.utils.dal.ClassifierDAL;
import edu.iastate.coms.cs472.newspet.utils.dal.DatabaseAccessLayer;
import edu.iastate.coms.cs472.newspet.utils.dal.TrainerCheckoutData;

public class TrainerThreadJob implements Runnable, Iterable<TrainingItem>
{	
	private List<TrainingItem> trainingItems;
	private int classifierID;
	
	public TrainerThreadJob(int classifierID)
	{
		trainingItems=new ArrayList<TrainingItem>();
		this.classifierID=classifierID;
	}
	
	public void run()
	{
		//get trainer (lock by ID)
		TrainerCheckoutData checkoutData=null;
		try
		{
			checkoutData = ClassifierDAL.getClassifier(classifierID);
		}
		catch(InterruptedException e)
		{
			
		} 
		
		try
		{
			//set up filters
			InstanceList trainingInstanceList = new  InstanceList(checkoutData.getPipe());
			trainingInstanceList.addThruPipe(new TrainingItemToInstanceIterator(trainingItems.iterator()));
		
			//train
			checkoutData.getTrainer().trainIncremental(trainingInstanceList);
		
			
		}
		finally
		{
			//persist
			//(will unlock by ID)
			//(DAL keeps checkoutdata in bookkeeping)
			ClassifierDAL.giveClassifier(classifierID);
		}
	}

	public Iterator<TrainingItem> iterator()
	{
		return trainingItems.iterator();
	}
	
	public void add(TrainingItem item)
	{
		trainingItems.add(item);
	}
}
