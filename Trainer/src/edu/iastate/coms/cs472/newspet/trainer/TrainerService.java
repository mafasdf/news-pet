package edu.iastate.coms.cs472.newspet.trainer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayes;
import cc.mallet.classify.NaiveBayesTrainer;
import edu.iastate.coms.cs472.newspet.utils.ClassifierDAL;
import edu.iastate.coms.cs472.newspet.utils.DocumentConversion;

/**
 * @author Michael Fulker
 */
public class TrainerService
{
	//TODO: finalize what type of element we want to queue, (and whether or not we use the BlockingQueue interface)
	private BlockingDeque<String> queue;
	
	private ThreadPoolExecutor threadPool;
	
	/**
	 * Constructor
	 * TODO: add params as needed 
	 */
	public TrainerService()
	{
		//TODO: change to Tyson's message queue
		queue = new LinkedBlockingDeque<String>();
		
		//TODO: fine-tune / have configurable params
		threadPool=new ThreadPoolExecutor(32, 32, 100, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//initialize DB connection
		//TODO
		
		(new TrainerService()).run();
	}
	
	private void run()
	{
		//TODO: artificial queue-priming for testing only
		queue.add("123,1,the quick brown fox jumped over the lazy dogs");
		queue.add("123,1,quick brown fox jumped over the lazy dogs the");
		queue.add("123,1,brown fox jumped over the lazy dogs the quick");
		queue.add("123,1,a fox jumps over a lazy dog");
		queue.add("123,1,the quick brown fox jumped");
		queue.add("123,0,superman vs. batman");
		queue.add("123,0,the little house on the prairie");
		queue.add("123,0,power level is over 9000 ");
		queue.add("123,0,lazy instantiation of variables");
		
		
		while(true)
		{
			String incomingString=blockingPeekInQueue();
			long currentClassifierID=convertMessageToTrainingItem(incomingString).getClassifierID();
			
			//get job of contiguous items for specific classifier
			TrainerThreadJob job = new TrainerThreadJob(currentClassifierID);
			TrainingElement currentItem;
			while(!queue.isEmpty() && (currentItem=convertMessageToTrainingItem(blockingPeekInQueue())).getClassifierID()==currentClassifierID)
			{
				job.add(currentItem);
				queue.remove();
			}
			
			
			
			//give to threadpool
			//TODO threadPool.submit(toRun);
			job.run(); //using sequential execution for now
			
			
			//TODO: only a hack test
			if(queue.isEmpty())
			{
				//try classifying something
				try
				{
					NaiveBayes classifier = ClassifierDAL.getTrainerByClassifierID(123).getClassifier();
					synchronized(classifier)
					{
						PrintWriter pw = new PrintWriter(System.out);
						classifier.classify("fox dogs jumped blah").print(pw);
						classifier.classify("little superman on the prairie").print(pw);
						classifier.classify("SOMETHING COMPLETELY DIFFERENT").print(pw);
						pw.flush();
					}
				}
				catch(Exception e)
				{
					throw new RuntimeException("We just lost the game"); 
				}	
				System.exit(0);
			}
		}
	}

	
	private String blockingPeekInQueue()
	{
		String toReturn = null; 
		//block until something in queue
		try
		{
			toReturn = queue.take();
			queue.putFirst(toReturn);
		}
		catch(InterruptedException e)
		{
			//TODO: lookup doc, is this the expected way to unblock?
			throw new RuntimeException("InterruptedException during queue block");
		}
		
		return toReturn;
	}

	private TrainingElement convertMessageToTrainingItem(String message)
	{
		// TODO: finalize expected string format and parse accordingly
		// TODO: handle jobs consisting of more than one item 
		int commaIndex0=message.indexOf(',');
		
		long classifierID = Long.parseLong(message.substring(0,commaIndex0));
		int commaIndex1=message.indexOf(',',commaIndex0+1);
		long categoryID = Long.parseLong(message.substring(commaIndex0 + 1,commaIndex1));
		String document = message.substring(commaIndex1+1);
		
		return new TrainingElement(classifierID,categoryID,document);
	}
}