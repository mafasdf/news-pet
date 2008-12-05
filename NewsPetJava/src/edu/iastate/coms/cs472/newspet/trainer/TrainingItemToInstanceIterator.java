package edu.iastate.coms.cs472.newspet.trainer;

import java.util.Iterator;

import cc.mallet.types.Instance;

/**
 * Converts a TrainingItem iterator to an Instance iterator.
 * 
 * @author Michael Fulker
 */
public class TrainingItemToInstanceIterator implements Iterator<Instance>
{
	private Iterator<TrainingItem> source;
	
	public TrainingItemToInstanceIterator(Iterator<TrainingItem> source)
	{
		this.source = source;
	}
	
	public boolean hasNext()
	{
		return source.hasNext();
	}
	
	public Instance next()
	{
		return source.next().getData();
	}
	
	public void remove()
	{
		source.remove();
	}
}
