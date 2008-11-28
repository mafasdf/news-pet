package edu.iastate.coms.cs472.newspet.utils;

import cc.mallet.classify.NaiveBayesTrainer;


public class ClassifierDAL
{	
	//TODO:remove
	private static NaiveBayesTrainer theTrainer=new NaiveBayesTrainer();
	
	public static NaiveBayesTrainer getTrainerByClassifierID(long classifierId)
	{
		//TODO: get from DB via serialization
		return theTrainer;
	}
}
