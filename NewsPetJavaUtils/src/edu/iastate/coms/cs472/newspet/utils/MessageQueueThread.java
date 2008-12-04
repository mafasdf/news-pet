package edu.iastate.coms.cs472.newspet.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.LinkedList;

public class MessageQueueThread extends Thread
{
	private boolean listeningForNewClients;

	private int port;
	private int timeout;

	private Collection<MessageQueueWorkerThread> workerThreads;

	private OurLinkedBlockingDeque<String> messageQueue = new OurLinkedBlockingDeque<String>();

	public MessageQueueThread(int port, int timeout)
	{
		if(port < 0 || port > 65535) throw new IllegalArgumentException("Port out of range: " + port);
		if(timeout < 0) throw new IllegalArgumentException("Timeout cannot be negative: " + timeout);

		this.port = port;
		this.timeout = timeout;
	}

	public MessageQueueThread(int port)
	{
		this(port, 1000);
	}

	public OurLinkedBlockingDeque<String> getMessageQueue()
	{
		return messageQueue;
	}

	@Override
	public void run()
	{
		ServerSocket serverSocket;
		try
		{
			serverSocket = new ServerSocket(port);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}

		try
		{
			serverSocket.setSoTimeout(timeout);
		}
		catch(SocketException e)
		{
			System.err.println("SocketException during setSoTimeout() for the ServerSocket: " + serverSocket);
			System.err.println(e.getMessage());
		}

		workerThreads = new LinkedList<MessageQueueWorkerThread>();

		listeningForNewClients = true;
		try
		{
			while(listeningForNewClients)
			{
				try
				{
					Socket clientSocket = serverSocket.accept();

					MessageQueueWorkerThread newWorkerThread = new MessageQueueWorkerThread(clientSocket);
					newWorkerThread.start();
					workerThreads.add(newWorkerThread);
				}
				catch(SocketTimeoutException e)
				{
					// do nothing
				}
				catch(IOException e)
				{
					System.err.println("IOException while trying to accept a new client connection!");
					System.err.println(e.getMessage());
				}
			}
		}
		finally
		{
			try
			{
				serverSocket.close();
			}
			catch(Exception e)
			{
				System.err.println(e.getClass().getName() + " while trying to close the ServerSocker: " + serverSocket);
				System.err.println(e.getMessage());
			}
		}
	}

	public void stopQueue()
	{
		listeningForNewClients = false;
		for(MessageQueueWorkerThread t : workerThreads)
		{
			t.stopListening();
		}
		for(MessageQueueWorkerThread t : workerThreads)
		{
			try
			{
				t.join();
			}
			catch(InterruptedException e)
			{
				System.err.println("InterruptedException while joined to a worker thread: " + t);
				System.err.println(e.getMessage());
			}
		}
	}

	private class MessageQueueWorkerThread extends Thread
	{
		private boolean listenToClient;

		private Socket clientSocket;

		public MessageQueueWorkerThread(Socket clientSocket)
		{
			this.clientSocket = clientSocket;
		}

		@Override
		public void run()
		{
			BufferedReader input;
			try
			{
				InputStream inputStream = clientSocket.getInputStream();
				input = new BufferedReader(new InputStreamReader(inputStream));
			}
			catch(IOException e)
			{
				String errorMessage = "IOException duing getInputStream() for Socket: " + clientSocket;
				System.err.println(errorMessage);
				throw new RuntimeException(errorMessage);
			}

			try
			{
				listenToClient = true;
				while(listenToClient)
				{
					try
					{
						if(input.ready())
						{
							String message = input.readLine();

							getMessageQueue().add(message);
						}
						try
						{
							Thread.sleep(1000);
						}
						catch(InterruptedException e)
						{
							/*
							 * TODO do nothing for now
							 */
						}
					}
					catch(IOException e)
					{
						System.err.println(e.getClass().getName() + " during readLine() for a BufferedReader!");
						System.err.println(e.getMessage());
					}
				}
			}
			finally
			{
				try
				{
					input.close();
				}
				catch(IOException e)
				{
					System.err.println("IOException while closing the a BufferedInputStream!");
					System.err.println(e.getMessage());
				}

				try
				{
					clientSocket.close();
				}
				catch(IOException e)
				{
					System.err.println("IOException while closing the client Socket: " + clientSocket);
					System.err.println(e.getMessage());
				}
			}
		}

		public void stopListening()
		{
			listenToClient = false;
			/*
			 * TODO can't figure out how to force dataIn.readUTF() to stop
			 * blocking. Probably is
			 */
		}
	}
}
