package edu.iastate.coms.cs472.newspet.trainer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cc.mallet.types.InstanceList;
import edu.iastate.coms.cs472.newspet.utils.dal.BatchTrainingSetDAL;
import edu.iastate.coms.cs472.newspet.utils.dal.ClassifierDAL;
import edu.iastate.coms.cs472.newspet.utils.dal.FeedItemDAL;
import edu.iastate.coms.cs472.newspet.utils.dal.TrainerCheckoutData;

public class TrainerThreadJob implements Runnable
{
	private List<Message> trainingMessages;
	
	private List<TrainingItem> trainingItems;
	
	private int classifierID;
	
	public TrainerThreadJob(int classifierID)
	{
		trainingItems = new ArrayList<TrainingItem>();
		trainingMessages = new ArrayList<Message>();
		this.classifierID = classifierID;
	}
	
	public void run()
	{
		//convert all messages into trainingitems
		for(Message message : trainingMessages)
		{
			if(message.getMessageType() == Message.MessageType.INCREMENTAL) trainingItems.add(new TrainingItem(classifierID, message.getCategoryId(), FeedItemDAL.getFeedItemText(message.getSourceId())));
			else if(message.getMessageType() == Message.MessageType.BATCH) addBatchItems(message.getSourceId(), message.getCategoryId());
		}
		
		//get trainer (lock by ID)
		TrainerCheckoutData checkoutData = null;
		try
		{
			checkoutData = ClassifierDAL.getClassifier(classifierID);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		try
		{
			//set up filters
			InstanceList trainingInstanceList = new InstanceList(checkoutData.getPipe());
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
	
	private void addBatchItems(int sourceId, int categoryId)
	{
		String path = BatchTrainingSetDAL.getPath(sourceId);
		
		Scanner in;
		try
		{
			in = new Scanner(new File(path));
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
			return;
		}
		
		while(in.hasNextLine())
		{
			TrainingItem toAdd = new TrainingItem(classifierID, categoryId, in.nextLine());
			trainingItems.add(toAdd);
		}
	}
	
	public void add(Message trainingMessage)
	{
		trainingMessages.add(trainingMessage);
	}
}
