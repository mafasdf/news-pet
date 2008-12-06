package edu.iastate.coms.cs472.newspet.analysis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayes;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;

import edu.iastate.coms.cs472.newspet.data.OriginalToNewFormat;
import edu.iastate.coms.cs472.newspet.reader.ItemClassificationJob;
import edu.iastate.coms.cs472.newspet.utils.DocumentProcessing;
import edu.iastate.coms.cs472.newspet.utils.Pair;
import edu.iastate.coms.cs472.newspet.utils.dal.ClassifierDAL;

public class AnalyzeClassifier
{
	
	private static Random random = new Random();
	
	public static void main(String[] args) throws IOException
	{
		double trainingRatio = Double.parseDouble(args[0]);
		
		//for each, run 16 trials
		for(int i = 0; i < 16; i++)
		{
			//66 non-trash categories
			double accuracyA = getAccuracy(trainingRatio, 66);
			//20 non-trash categories
			double accuracyB = getAccuracy(trainingRatio, 30);
			//5 non-trash categories
			double accuracyC = getAccuracy(trainingRatio, 5);
			
			//output in form easily consumable by PyX
			System.out.println(trainingRatio + "\t" + accuracyA + "\t" + accuracyB + "\t" + accuracyC);
		}
	}
	
	/**
	 * @param trainingRatio
	 *        The ratio for how much input is for training (the rest is left for
	 *        testing).
	 * @param numCategories
	 *        If this number is less than the number of categories found,
	 *        randomly selected categories will all be renamed to "trash" such
	 *        that the number of non-trash categories is equal to the argument.
	 * @return
	 * @throws IOException
	 */
	private static double getAccuracy(double trainingRatio, int numCategories) throws IOException
	{
		List<Pair<String, String>> trainingExamples = OriginalToNewFormat.getDataset(true);
		
		//extract the unique categories
		HashSet<String> categories = new HashSet<String>();
		for(Pair<String, String> item : trainingExamples)
		{
			categories.add(item.getA());
		}
		List<String> categoriesList = new ArrayList<String>(categories);
		
		//remove random categories until we have numCategories
		while(categories.size() > numCategories)
			categories.remove(categoriesList.remove(random.nextInt(categoriesList.size())));
		
		//relabel the training samples not in this set of categories as "trash"
		for(Pair<String, String> item : trainingExamples)
			if(!categories.contains(item.getA())) item.setA("trash");
		
		//extract out the testing values
		int trainingSize = (int) Math.ceil(trainingRatio * trainingExamples.size());
		List<Pair<String, String>> testItems = new ArrayList<Pair<String, String>>(trainingExamples.size() - trainingSize);
		while(trainingExamples.size() != trainingSize)
		{
			int indexToRemoveAt = random.nextInt(trainingExamples.size());
			Pair<String, String> item = trainingExamples.remove(indexToRemoveAt);
			testItems.add(item);
		}
		
		//set up trainer
		ClassifierTrainer<NaiveBayes> trainer = ClassifierDAL.createClassifierTrainer();
		Pipe pipes = DocumentProcessing.createConversionPipes();
		//set up filters
		InstanceList trainingInstanceList = new InstanceList(pipes);
		trainingInstanceList.addThruPipe(new PairToInstanceIterator(trainingExamples.iterator()));
		
		//train
		for(int i = 0; i < 10; i++)
			trainer.train(trainingInstanceList);
		
		//test on remaining instances
		Classifier classifier = trainer.getClassifier();
		
		int numCorrect = 0;
		int numUncertain = 0;
		for(Pair<String, String> testingItem : testItems)
		{
			Classification classificationResults = classifier.classify(testingItem.getB());
			
			Label bestLabel = classificationResults.getLabelVector().getBestLabel();
			
			//check for uncertainty
			if(classificationResults.getLabelVector().getMaxValue() * classificationResults.getLabelVector().getValues().length < ItemClassificationJob.PROBABILITY_SIGNIFICANCE_RATIO)
				numUncertain++;
			else if(testingItem.getA().equals(bestLabel.getEntry())) numCorrect++;
		}
		
		return numCorrect * 100.0 / testItems.size();
	}
	
	private static class PairToInstanceIterator implements Iterator<Instance>
	{
		private Iterator<Pair<String, String>> iter;
		
		public PairToInstanceIterator(Iterator<Pair<String, String>> iter)
		{
			this.iter = iter;
		}
		
		public boolean hasNext()
		{
			return iter.hasNext();
		}
		
		public Instance next()
		{
			Pair<String, String> item = iter.next();
			return new Instance(item.getB(), item.getA(), null, null);
		}
		
		public void remove()
		{
			throw new NotImplementedException();
		}
	}
	
}
