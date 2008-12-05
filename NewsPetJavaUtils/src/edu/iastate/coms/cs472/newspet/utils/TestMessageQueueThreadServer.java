package edu.iastate.coms.cs472.newspet.utils;

public class TestMessageQueueThreadServer
{
	public static void main(final String[] args) throws InterruptedException
	{
		final int port = 8000;
		testServerEcho(port);
	}
	
	public static void testServerEcho(int port) throws InterruptedException
	{
		MessageQueueThread mqt = new MessageQueueThread(port);
		mqt.start();
		
		while(true)
		{
			String peekString = mqt.getMessageQueue().blockingPeek();
			System.out.println("peekString: " + peekString);
			
			String takeString = mqt.getMessageQueue().take();
			System.out.println("takeString: " + takeString);
		}
	}
}
