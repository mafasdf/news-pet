package edu.iastate.coms.cs472.newspet.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestMessageQueueThreadClient
{
	public static void main(String[] args) throws UnknownHostException, IOException
	{
		int port = 8000;
		testJavaClient("localhost", port);
	}
	
	private static void testJavaClient(String host, int port) throws UnknownHostException, IOException
	{
		Socket socket = new Socket(host, port);
		DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
		
		for(int i = 0; i < 10; i++)
		{
			String s = "" + i;
			dataOut.write(s.getBytes());
		}
		
		dataOut.close();
		socket.close();
	}	
}
