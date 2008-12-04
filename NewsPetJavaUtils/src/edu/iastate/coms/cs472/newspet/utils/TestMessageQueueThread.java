package edu.iastate.coms.cs472.newspet.utils;

public class TestMessageQueueThread
{
	public static void main(String[] args) throws InterruptedException
	{
		testEcho();
	}
	
	public static void testEcho() throws InterruptedException
	{
		MessageQueueThread mqt = new MessageQueueThread(8000);
		mqt.run();
		
		while(true)
		{
			String peekString = mqt.getMessageQueue().blockingPeek();
			System.out.println("peekString: " + peekString);
			
			String takeString = mqt.getMessageQueue().take();
			System.out.println("takeString: " + takeString);
		}
	}
}
