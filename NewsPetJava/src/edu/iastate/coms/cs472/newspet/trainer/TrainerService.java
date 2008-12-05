package edu.iastate.coms.cs472.newspet.trainer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayes;
import cc.mallet.classify.NaiveBayesTrainer;
import edu.iastate.coms.cs472.newspet.utils.DocumentConversion;
import edu.iastate.coms.cs472.newspet.utils.MessageQueueThread;
import edu.iastate.coms.cs472.newspet.utils.dal.DatabaseAccessLayer;

/**
 * @author Michael Fulker
 */
public class TrainerService
{
	//TODO: finalize what type of element we want to queue, (and whether or not we use the BlockingQueue interface)
	private MessageQueueThread queue;
	
	private ThreadPoolExecutor threadPool;
	
	public TrainerService(int port, int timeout)
	{
		if(timeout == -1)
			queue = new MessageQueueThread(port);
		else
			queue = new MessageQueueThread(port, timeout);
		
		//TODO: fine-tune / have configurable params
		threadPool = new ThreadPoolExecutor(32, 32, 100, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		if(args.length < 1) throw new IllegalArgumentException("Requires at least one argument for message listening port");
		
		int port = Integer.parseInt(args[0]);
		
		int timeout;
		if(args.length >= 2)
			timeout = Integer.parseInt(args[1]);
		else
			timeout = -1;
		
		(new TrainerService(port, timeout)).run();
	}
	
	private void run()
	{
		while(true)
		{
			String incomingString = blockingPeekInQueue();
			int currentClassifierID = convertMessageToTrainingItem(incomingString).getClassifierID();
			
			//get job of contiguous items for specific classifier
			TrainerThreadJob job = new TrainerThreadJob(currentClassifierID);
			TrainingItem currentItem;
			while(!queue.getMessageQueue().isEmpty()
					&& (currentItem = convertMessageToTrainingItem(blockingPeekInQueue())).getClassifierID() == currentClassifierID)
			{
				job.add(currentItem);
				queue.getMessageQueue().remove();
			}
			
			//give to threadpool
			threadPool.execute(job);
		}
		
		//TODO: have input-triggered clean shutdown threadPool.shutdown();
	}
	

	private String blockingPeekInQueue()
	{
		//block until something in queue
		try
		{
			return queue.getMessageQueue().blockingPeek();
		}
		catch(InterruptedException e)
		{
			throw new RuntimeException("InterruptedException during queue block", e);
		}
	}
	
	private TrainingItem convertMessageToTrainingItem(String message)
	{
		// TODO: finalize expected string format and parse accordingly
		// TODO: handle jobs consisting of more than one item 
		int commaIndex0 = message.indexOf(',');
		
		int classifierID = Integer.parseInt(message.substring(0, commaIndex0));
		int commaIndex1 = message.indexOf(',', commaIndex0 + 1);
		int categoryID = Integer.parseInt(message.substring(commaIndex0 + 1, commaIndex1));
		String document = message.substring(commaIndex1 + 1);
		
		return new TrainingItem(classifierID, categoryID, document);
	}
}
