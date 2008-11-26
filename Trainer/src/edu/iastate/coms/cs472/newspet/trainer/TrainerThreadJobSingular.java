package edu.iastate.coms.cs472.newspet.trainer;

import edu.iastate.coms.cs472.newspet.utils.ClassifierDAL;
import edu.iastate.coms.cs472.newspet.utils.DocumentConversion;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;

public class TrainerThreadJobSingular extends TrainerThreadJob
{
	private TrainingItem trainingItem;
	
	public TrainerThreadJobSingular(TrainingItem trainingItem)
	{
		this.trainingItem = trainingItem;
	}
	
	public void run()
	{
		
		//get classifier from DAL
		NaiveBayesUpdateable classifier = ClassifierDAL.getClassifierByID(trainingItem.getClassifierID());
		
		Instances instances = DocumentConversion.documentToSingletonInstance(trainingItem.getDocumentText(),trainingItem.getCategoryID());
		
		
		
		//TODO: figure out better (DB) locking (in case multiple jobs share a classifier)
		synchronized(classifier)
		{
			try
			{
				classifier.updateClassifier(instances.firstInstance());
			}
			catch(Exception e)
			{
				throw new RuntimeException("Following document instance could not be added to classifier: "+trainingItem.getDocumentText()); 
			}
		}
	}
	
}
