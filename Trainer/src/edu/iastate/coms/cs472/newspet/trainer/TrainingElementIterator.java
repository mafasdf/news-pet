package edu.iastate.coms.cs472.newspet.trainer;

import java.util.Iterator;

import cc.mallet.types.Instance;

/**
 * Converts a TrainingItem iterator to an Instance iterator. 
 * @author Michael Fulker
 */
public class TrainingElementIterator implements Iterator<Instance>
{
	private Iterator<TrainingElement> source;
	
	public TrainingElementIterator(Iterator<TrainingElement> source)
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
	
	@Override
	public void remove()
	{
	// TODO Auto-generated method stub
	
	}
	
}
