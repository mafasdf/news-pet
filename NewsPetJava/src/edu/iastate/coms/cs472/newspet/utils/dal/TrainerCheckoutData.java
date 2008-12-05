package edu.iastate.coms.cs472.newspet.utils.dal;

import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.Pipe;

/**
 * Class for returning multiple values in
 * {@link DatabaseAccessLayer#getTrainerForUpdating(int)}
 * 
 * @author Michael Fulker
 */
public class TrainerCheckoutData
{
	private NaiveBayesTrainer trainer;
	private Pipe pipe;
	private int classifierID;
	
	public TrainerCheckoutData(NaiveBayesTrainer trainer, Pipe pipe, int classifierID)
	{
		this.trainer = trainer;
		this.pipe = pipe;
		this.classifierID = classifierID;
	}
	
	public NaiveBayesTrainer getTrainer()
	{
		return trainer;
	}
	
	public Pipe getPipe()
	{
		return pipe;
	}
	
	public int getClasifierID()
	{
		return classifierID;
	}
}
