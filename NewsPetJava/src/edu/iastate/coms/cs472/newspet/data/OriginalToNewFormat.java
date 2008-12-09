package edu.iastate.coms.cs472.newspet.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import edu.iastate.coms.cs472.newspet.utils.Pair;

public class OriginalToNewFormat
{
	private static final String TOPIC_OPEN_ELEMENT = "<TOPICS>";
	
	private static final String TOPIC_CLOSE_ELEMENT = "</TOPICS>";
	
	private static final String TITLE_OPEN_ELEMENT = "<TITLE>";
	
	private static final String TITLE_CLOSE_ELEMENT = "</TITLE>";
	
	private static final String BODY_OPEN_ELEMENT = "<BODY>";
	
	private static final String BODY_CLOSE_ELEMENT = "</BODY>";
	
	private static final String PATH_TO_OLD_FILES = "data" + File.separator + "_Original Data_" + File.separator;
	
	private static final String PATH_TO_NEW_FILES = "data" + File.separator;
	
	private static final String PATH_TO_ALL_TOPICS_FILE = PATH_TO_OLD_FILES + "all-topics-strings.lc.txt";
	
	public enum State
	{
		LOOKING_FOR_TOPIC, LOOKING_FOR_TITLE, LOOKING_FOR_BODY
	}
	
	public static void main(String[] args) throws IOException
	{
		Boolean returnList = false;
		getDataset(returnList);
	}

	public static List<Pair<String, String>> getDataset(boolean returnList) throws IOException
	{
		Map<String, PrintWriter> mapTopicsToPrintWriters = getMapToTopicFiles(returnList);
		
//		int count = 0;
		for(int i = 0; i <= 21; i++)
		{
			String topic = null;
			String fileName = getFileName(i);
			Scanner fileIn = new Scanner(new File(fileName));
			
			State state = State.LOOKING_FOR_TOPIC;
			while(fileIn.hasNextLine())
			{
				String line = fileIn.nextLine();
				
				switch(state)
				{
				case LOOKING_FOR_TOPIC:
					if(line.contains(TOPIC_OPEN_ELEMENT))
					{
						final String openElement = "<D>";
						final String closeElement = "</D>";
						
						int index = line.indexOf(openElement);
						if(index == -1) break;
						
						//remove articles with multiple topics
						if(index != line.lastIndexOf(openElement)) break;
						
						int startIndex = index + openElement.length();
						int endIndex = line.indexOf(closeElement);
						topic = line.substring(startIndex, endIndex);
						
						state = State.LOOKING_FOR_TITLE;
					}
					break;
				case LOOKING_FOR_TITLE:
					if(line.contains(TITLE_OPEN_ELEMENT))
					{
						line = line.substring(line.indexOf(TITLE_OPEN_ELEMENT) + TITLE_OPEN_ELEMENT.length(), line.length() - TITLE_CLOSE_ELEMENT.length());
						line = doRegularExpressionReplacements(line);
						
						PrintWriter topicPW = mapTopicsToPrintWriters.get(topic);
						topicPW.print(line);
						
						state = State.LOOKING_FOR_BODY;
					}
					break;
				case LOOKING_FOR_BODY:
					if(line.contains(BODY_OPEN_ELEMENT))
					{
//						count++;
//						if(count == 394)
//						{
//							System.out.println();
//						}
						line = line.substring(line.indexOf(BODY_OPEN_ELEMENT) + BODY_OPEN_ELEMENT.length());
						StringBuilder sb = new StringBuilder();
						while(!line.contains(BODY_CLOSE_ELEMENT))
						{
							line = doRegularExpressionReplacements(line);
							sb.append(" " + line);
							line = fileIn.nextLine();
						}
						int endIndex = line.indexOf(BODY_CLOSE_ELEMENT);
						sb.append(doRegularExpressionReplacements(line.substring(0, endIndex)));
						
						PrintWriter topicPW = mapTopicsToPrintWriters.get(topic);
						topicPW.println(sb.toString());
						
						state = State.LOOKING_FOR_TOPIC;
					}
					break;
				}
			}
		}
		
		if(returnList)
		{
			int goodGuess = 9003;
			List<Pair<String, String>> list = new ArrayList<Pair<String, String>>(goodGuess);
			for(String topic : mapTopicsToPrintWriters.keySet())
			{
				for(String body : ((MyPrintWriter)mapTopicsToPrintWriters.get(topic)).getList())
				{
					list.add(new Pair<String, String>(topic, body));
				}
			}
			return list;
		}
		
		for(String topic : mapTopicsToPrintWriters.keySet())
		{
			mapTopicsToPrintWriters.get(topic).close();
		}
		
		//not return value needed
		return null;
	}
	
	private static String doRegularExpressionReplacements(String line)
	{
		line = line.replaceAll("&#[0-9]*;", "");
		line = line.replaceAll("&lt;", "<");
		return line;
	}
	
	private static Map<String, PrintWriter> getMapToTopicFiles(boolean returnList) throws IOException
	{
		Scanner fileIn = new Scanner(new File(PATH_TO_ALL_TOPICS_FILE));
		
		Map<String, PrintWriter> mapTopicsToFiles = new TreeMap<String, PrintWriter>();
		
		while(fileIn.hasNextLine())
		{
			String topic = fileIn.nextLine().trim();
			PrintWriter topicPW;
			if(returnList)
			{
				topicPW = new MyPrintWriter();
			}
			else
			{
				topicPW = new PrintWriter(new BufferedWriter(new FileWriter(PATH_TO_NEW_FILES + topic + ".txt")));
			}
			mapTopicsToFiles.put(topic, topicPW);
		}
		
		return mapTopicsToFiles;
	}
	
	private static String getFileName(int i)
	{
		String fileNumber = "" + i;
		while(fileNumber.length() != 3)
		{
			fileNumber = "0" + fileNumber;
		}
		
		return PATH_TO_OLD_FILES + "reut2-" + fileNumber + ".sgm";
	}
	
	private static class MyPrintWriter extends PrintWriter
	{
		private String temp = "";
		
		private List<String> list = new LinkedList<String>();
		
		public MyPrintWriter()
		{
			super(new StringWriter());
		}
		
		public List<String> getList()
		{
			return list;
		}
		
		@Override
		public void print(String s)
		{
			temp = s + temp;
		}
		
		@Override
		public void println(String s)
		{
			list.add(temp + s);
			temp = "";
		}
	}
}
