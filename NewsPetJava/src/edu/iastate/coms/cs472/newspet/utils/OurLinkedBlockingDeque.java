package edu.iastate.coms.cs472.newspet.utils;

import java.util.concurrent.LinkedBlockingDeque;

public class OurLinkedBlockingDeque<E> extends LinkedBlockingDeque<E>
{
	private static final long serialVersionUID = 1L;
	
	private Object lock = new Object();
	
	public E blockingPeek() throws InterruptedException
	{
		E e = super.peek();
		if(e != null) return e;
		
		synchronized(lock)
		{
			//check again
			e = super.peek();
			if(e != null) return e;
			
			lock.wait();
		}
		
		return blockingPeek();
	}
	
	@Override
	public boolean add(E e)
	{
		boolean retVal = super.add(e);
		
		synchronized(lock)
		{
			lock.notifyAll();
		}
		
		return retVal;
	}
}
