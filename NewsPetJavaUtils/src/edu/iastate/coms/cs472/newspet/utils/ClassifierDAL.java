package edu.iastate.coms.cs472.newspet.utils;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.bayes.NaiveBayesUpdateable;


public class ClassifierDAL
{	
	//TODO
	private static NaiveBayesUpdateable theClassifier=new NaiveBayesUpdateable();
	
	//TODO: use UpdateableClassifier interface?
	public static NaiveBayesUpdateable getClassifierByID(long classifierId)
	{
		//TODO: get from DB via serialization
		return theClassifier;
	}
}
