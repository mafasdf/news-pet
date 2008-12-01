package edu.iastate.coms.cs472.newspet.utils;

import java.io.Serializable;

import cc.mallet.classify.NaiveBayes;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.Pipe;

/**
 * The 3 classifier-related objects that need to be persisted are: ClassifierTrainer, Classifier, and Pipe.
 * Since these share certain objects as member variables, it is more efficient to serialize them in a group, such as in this object.   
 * @author Michael Fulker
 */
public class ClassifierObjectGroup implements Serializable
{	
	private NaiveBayes classifier;
	private NaiveBayesTrainer trainer;
	private Pipe pipe;
	/**
	 * @param classifier
	 * @param trainer
	 * @param pipe
	 */
	public ClassifierObjectGroup(NaiveBayes classifier, NaiveBayesTrainer trainer, Pipe pipe)
	{
		this.classifier = classifier;
		this.trainer = trainer;
		this.pipe = pipe;
	}
	
	public NaiveBayes getClassifier()
	{
		return classifier;
	}
	public NaiveBayesTrainer getTrainer()
	{
		return trainer;
	}
	public Pipe getPipe()
	{
		return pipe;
	}
	
	
}
