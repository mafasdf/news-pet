package edu.iastate.coms.cs472.newspet.trainer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import weka.classifiers.Classifier;

import edu.iastate.coms.cs472.newspet.utils.ClassifierDAL;
import edu.iastate.coms.cs472.newspet.utils.DocumentConversion;

/**
 * @author Michael Fulker
 */
public class TrainerService
{
	
	
	//TODO: finalize what type of element we want to queue, (and wether or not we use the BlockingQueue interface)
	private BlockingQueue<String> queue;
	
	private ThreadPoolExecutor threadPool;
	
	/**
	 * Constructor
	 * TODO: add params as needed 
	 */
	public TrainerService()
	{
		//TODO: change to Tyson's message queue
		queue = new LinkedBlockingQueue<String>();
		
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
			String incomingString=null;
			
			//block until something in queue
			try
			{
				incomingString = queue.take();
			}
			catch(InterruptedException e)
			{
				//TODO: lookup doc, is this the expected way to unblock?
				throw new RuntimeException();
			}  
			
			//try to convert to trainer job 
			TrainerThreadJob toRun = convertMessageToTrainerJob(incomingString);
			if(toRun==null)
			{
				System.err.println("ERROR: could not parse the following message string:");
				System.err.println(incomingString);
				continue;
			}
			
			//give to threadpool
			//TODO threadPool.submit(toRun);
			toRun.run(); //using sequential execution for now
			
			
			
			//TODO: only a hack test
			if(queue.isEmpty())
			{
				//try classifying something
				try
				{
					Classifier classifier = ClassifierDAL.getClassifierByID(123);
					synchronized(classifier)
					{
						System.out.println(classifier.classifyInstance(DocumentConversion.documentToSingletonInstance("the quick brown fox jumped over the lazy dogs", 0).firstInstance()));
					}
				}
				catch(Exception e)
				{
					throw new RuntimeException("We just lost the game"); 
				}	
			}
		}
	}

	
	private TrainerThreadJob convertMessageToTrainerJob(String message)
	{
		// TODO: finalize expected string format and parse accordingly
		// TODO: handle jobs consisting of more than one item 
		int commaIndex0=message.indexOf(',');
		
		long classifierID = Long.parseLong(message.substring(0,commaIndex0));
		int commaIndex1=message.indexOf(',',commaIndex0+1);
		long categoryID = Long.parseLong(message.substring(commaIndex0 + 1,commaIndex1));
		String document = message.substring(commaIndex1+1);
		
		return new TrainerThreadJobSingular(new TrainingItem(document,classifierID,categoryID));
	}
}