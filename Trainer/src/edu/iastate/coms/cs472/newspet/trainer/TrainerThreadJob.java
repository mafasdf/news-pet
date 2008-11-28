package edu.iastate.coms.cs472.newspet.trainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.types.InstanceList;
import edu.iastate.coms.cs472.newspet.utils.ClassifierDAL;
import edu.iastate.coms.cs472.newspet.utils.DocumentConversion;

public class TrainerThreadJob implements Runnable, Iterable<TrainingElement>
{	
	private List<TrainingElement> trainingItems;
	private long classifierID;
	
	public TrainerThreadJob(long classifierID)
	{
		trainingItems=new ArrayList<TrainingElement>();
		this.classifierID=classifierID;
	}
	
	public void run()
	{
		//TODO: lock properly
		NaiveBayesTrainer trainer = ClassifierDAL.getTrainerByClassifierID(classifierID); 
		
		InstanceList trainingInstanceList = new  InstanceList(DocumentConversion.getConversionPipes());
		trainingInstanceList.addThruPipe(new TrainingElementIterator(trainingItems.iterator()));
		
		trainer.trainIncremental(trainingInstanceList);
	}

	public Iterator<TrainingElement> iterator()
	{
		return trainingItems.iterator();
	}
	
	public void add(TrainingElement item)
	{
		trainingItems.add(item);
	}
}
