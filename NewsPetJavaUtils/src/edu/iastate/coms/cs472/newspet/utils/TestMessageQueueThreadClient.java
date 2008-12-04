package edu.iastate.coms.cs472.newspet.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
		BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		for(int i = 0; i < 10; i++)
		{
			String s = i + "\n";
			output.write(s);
		}
		
		output.close();
		socket.close();
	}	
}
