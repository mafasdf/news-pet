package edu.iastate.coms.cs472.newspet.utils.dal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ClassifierDAL
{
	private static Map<Integer, GateKeeper> classifierIDsToGateKeeper = new HashMap<Integer, GateKeeper>();
	
	public static TrainerCheckoutData getClassifier(int classifierID) throws InterruptedException
	{
		GateKeeper gk = classifierIDsToGateKeeper.get(classifierID);
		if(gk == null)
		{
			synchronized(classifierIDsToGateKeeper)
			{
				//check again
				gk = classifierIDsToGateKeeper.get(classifierID);
				if(gk == null)
				{
					gk = new GateKeeper(classifierID);
					classifierIDsToGateKeeper.put(classifierID, gk);
				}
			}
		}
		
		return gk.getClasisifier();
	}
	
	public static void giveClassifier(int classifierID)
	{
		GateKeeper gk = classifierIDsToGateKeeper.get(classifierID);
		gk.giveClassifier();
	}
	
	private static class GateKeeper
	{
		private int classifierID;
		
		private TrainerCheckoutData classifier;
		
		private ReentrantLock semaphore = new ReentrantLock();
		
		private int semaphoreCount = 0;
		
		public GateKeeper(int classifierID)
		{
			this.classifierID = classifierID;
		}
		
		public synchronized TrainerCheckoutData getClasisifier() throws InterruptedException
		{
			if(classifier == null)
			{
				classifier = DatabaseAccessLayer.getTrainerForUpdating(this.classifierID);
			}
			
			if(semaphoreCount != 0)
			{
				this.wait();
			}
			semaphoreCount++;
			semaphore.lock();
			
			return classifier;
		}
		
		public synchronized void giveClassifier()
		{
			if(!semaphore.isHeldByCurrentThread()) return;
			
			DatabaseAccessLayer.updateTrainerAndClassifier(classifier);
			
			semaphoreCount--;
			semaphore.unlock();
			if(semaphoreCount == 0)
			{
				classifier = null;
			}
			else
			{
				this.notify();
			}
		}
	}
}
