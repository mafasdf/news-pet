package edu.iastate.coms.cs472.newspet.utils.dal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ClassifierDAL
{
	Map<Integer, GateKeeper> classifierIDsToGateKeeper = new HashMap<Integer, GateKeeper>();
	
	public TrainerCheckoutData getClassifier(int classifierID)
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
	
	public void giveClassifier(int classifierID)
	{
		GateKeeper gk = classifierIDsToGateKeeper.get(classifierID);
		gk.giveClassifier();
	}
	
	private class GateKeeper
	{
		private int classifierID;
		
		private TrainerCheckoutData classifier;
		
		private ReentrantLock semaphore = new ReentrantLock();
		
		private int semaphoreCount = 0;
		
		public GateKeeper(int classifierID)
		{
			this.classifierID = classifierID;
		}

		public synchronized TrainerCheckoutData getClasisifier()
		{
			if(classifier == null)
			{
				//TODO get from DatabaseAccessLayer
				classifier = null;
			}
			
			semaphoreCount++;
			semaphore.lock();
			
			return classifier;
		}

		public synchronized void giveClassifier()
		{
			if(!semaphore.isHeldByCurrentThread()) return;
			
			//TODO write classifier to database
			
			semaphoreCount--;
			if(semaphoreCount == 0)
			{
				classifier = null;
			}
		}
	}
}
