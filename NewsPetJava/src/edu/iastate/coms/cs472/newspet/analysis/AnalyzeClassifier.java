package edu.iastate.coms.cs472.newspet.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import edu.iastate.coms.cs472.newspet.data.OriginalToNewFormat;
import edu.iastate.coms.cs472.newspet.reader.ItemClassificationJob;
import edu.iastate.coms.cs472.newspet.trainer.TrainingItemToInstanceIterator;
import edu.iastate.coms.cs472.newspet.utils.DocumentProcessing;
import edu.iastate.coms.cs472.newspet.utils.Pair;
import edu.iastate.coms.cs472.newspet.utils.dal.ClassifierDAL;

public class AnalyzeClassifier
{	
	//the ratio for how much input is for training (the rest is left for testing)
	private static final double TRAINING_RATIO = 0.001;
	
	public static void main(String[] args) throws IOException
	{
		Random rnd = new Random();
		
		List<Pair<String,String>> trainingExamples = OriginalToNewFormat.getDataset(true);
		
		//extract out the testing values
		int trainingSize = (int) Math.ceil(TRAINING_RATIO * trainingExamples.size());
		List<Pair<String,String>> testItems = new ArrayList<Pair<String,String>>(trainingExamples.size() - trainingSize);
		while(trainingExamples.size()!=trainingSize)
		{
			int indexToRemoveAt = rnd.nextInt(trainingExamples.size());
			Pair<String,String> item = trainingExamples.remove(indexToRemoveAt);
			testItems.add(item);
		}
		
		//set up trainer
		NaiveBayesTrainer trainer = ClassifierDAL.createClassifierTrainer();
		Pipe pipes = DocumentProcessing.createConversionPipes();
		Iterator<Pair<String,String>> trainingIter = trainingExamples.iterator();
		//set up filters
		InstanceList trainingInstanceList = new InstanceList(pipes);
		trainingInstanceList.addThruPipe(new PairToInstanceIterator(trainingExamples.iterator()));
		
		//train
		trainer.train(trainingInstanceList);
		
		
		//check
		
		Classifier classifier = trainer.getClassifier();
		
		int numCorrect=0;
		int numUncertain=0;
		for(Pair<String,String> testingItem : testItems)
		{
			Classification classificationResults = classifier.classify(testingItem.getB());

			//check for uncertainty
			if(classificationResults.getLabelVector().getBestValue() * classificationResults.getLabelVector().getValues().length > ItemClassificationJob.PROBABILITY_SIGNIFICANCE_RATIO)
				numUncertain++;
			else if(testingItem.getA().equals(classificationResults.getLabelVector().getBestLabel().getEntry()))
				numCorrect++;
		}
		
		System.out.println("Total number of test instances:"+testItems.size());
		System.out.println("number of correctly classified instances:"+numCorrect);
		System.out.println("number of uncertain instances:"+numUncertain);
	}
	
	private static class PairToInstanceIterator implements Iterator<Instance>
	{
		private Iterator<Pair<String,String>> iter;
		public PairToInstanceIterator(Iterator<Pair<String,String>> iter)
		{this.iter=iter;}
		public boolean hasNext(){return iter.hasNext();}
		public Instance next()
		{
			Pair<String,String> item = iter.next();
			return new Instance(item.getB(), item.getA(), null, null);
		}
		public void remove(){throw new NotImplementedException();}
	}
}
