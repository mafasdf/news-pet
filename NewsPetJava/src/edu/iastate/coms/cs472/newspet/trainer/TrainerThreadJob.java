package edu.iastate.coms.cs472.newspet.trainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;
import edu.iastate.coms.cs472.newspet.utils.DocumentConversion;
import edu.iastate.coms.cs472.newspet.utils.dal.ClassifierDAL;
import edu.iastate.coms.cs472.newspet.utils.dal.ClassifierDAL.TrainerCheckoutData;

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
		TrainerCheckoutData checkoutData = ClassifierDAL.getTrainerForUpdating(classifierID); 
		
		//set up filters
		InstanceList trainingInstanceList = new  InstanceList(checkoutData.getPipe());
		trainingInstanceList.addThruPipe(new TrainingItemToInstanceIterator(trainingItems.iterator()));
		
		//train
		checkoutData.getTrainer().trainIncremental(trainingInstanceList);
		
		//persist
		//(will unlock by ID)
		ClassifierDAL.updateTrainerAndClassifier(checkoutData);
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
