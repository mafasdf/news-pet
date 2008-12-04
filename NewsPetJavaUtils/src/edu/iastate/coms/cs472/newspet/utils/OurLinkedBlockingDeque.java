package edu.iastate.coms.cs472.newspet.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class OurLinkedBlockingDeque<E> extends LinkedBlockingDeque<E>
{	
	private static final long serialVersionUID = 1L;
	
	private List<Thread> threadsBlockedOnPeek = new LinkedList<Thread>();
	
	public E blockingPeek() throws InterruptedException
	{
		E e = super.peek();
		if(e != null) return e;
		
		synchronized(threadsBlockedOnPeek)
		{
			//check again
			e = super.peek();
			if(e != null) return e;
			
			threadsBlockedOnPeek.add(Thread.currentThread());
		}
		
		this.wait();
		return blockingPeek();
	}
	
	@Override
	public boolean add(E e)
	{
		boolean retVal = super.add(e);
		
		synchronized(threadsBlockedOnPeek)
		{
			for(Thread t : threadsBlockedOnPeek)
			{
				t.notify();
			}
		}
		
		return retVal;
	}
}
