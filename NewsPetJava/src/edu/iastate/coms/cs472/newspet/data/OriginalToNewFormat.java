package edu.iastate.coms.cs472.newspet.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class OriginalToNewFormat
{
	private static final String TOPIC_OPEN_ELEMENT = "<TOPICS>";
	private static final String TOPIC_CLOSE_ELEMENT = "</TOPICS>";
	private static final String TITLE_OPEN_ELEMENT = "<TITLE>";
	private static final String TITLE_CLOSE_ELEMENT = "</TITLE>";
	private static final String BODY_OPEN_ELEMENT = "<BODY>";
	private static final String BODY_CLOSE_ELEMENT = "</BODY>";
	
	private static final String PATH_TO_OLD_FILES = "data" + File.pathSeparator + "_Original Data_" + File.pathSeparator;
	private static final String PATH_TO_NEW_FILES = "data" + File.pathSeparator;
	private static final String PATH_TO_ALL_TOPICS_FILE = PATH_TO_OLD_FILES + "all-topics-strings.lc.txt";
	
	public enum State { LOOKING_FOR_TOPIC, LOOKING_FOR_TITLE, LOOKING_FOR_BODY }
	
	public static void main(String[] args) throws IOException
	{
		Map<String, PrintWriter> mapTopicsToPrintWriters = getMapToTopicFiles();
		
		for(int i = 0; i <= 21; i++)
		{
			String fileName = getFileName(i);
			Scanner fileIn = new Scanner(new File(fileName));
			
			State state = State.LOOKING_FOR_TOPIC;
			Set<String> topics = new TreeSet<String>();
			while(fileIn.hasNextLine())
			{
				String line = fileIn.nextLine();
				
				switch(state)
				{
				case LOOKING_FOR_TOPIC:
					if(line.startsWith(TOPIC_OPEN_ELEMENT))
					{
						topics.clear();
						
						final String openElement = "<D>";
						final String closeElement = "</D>";
						
						for(int startIndex = TOPIC_OPEN_ELEMENT.length(); startIndex < line.length(); )
						{
							int index = line.indexOf(openElement, startIndex);
							if(index == -1) break;
							startIndex = line.indexOf(closeElement);
							String topic = line.substring(index + openElement.length(), startIndex);
							topics.add(topic);
						}
						
						state = State.LOOKING_FOR_TITLE;
					}
					break;
				case LOOKING_FOR_TITLE:
					if(line.startsWith(TITLE_OPEN_ELEMENT))
					{
						line = line.substring(TITLE_OPEN_ELEMENT.length(), line.length() - TITLE_CLOSE_ELEMENT.length());
						line = doRegularExpressionReplacements(line);
						
						for(String topic : topics)
						{
							PrintWriter topicPW = mapTopicsToPrintWriters.get(topic);
							topicPW.print(line + " ");
						}
						
						state = State.LOOKING_FOR_BODY;
					}
					break;
				case LOOKING_FOR_BODY:
					if(line.startsWith(BODY_OPEN_ELEMENT))
					{
						line = line.substring(BODY_OPEN_ELEMENT.length());
						StringBuilder sb = new StringBuilder();
						while(!line.contentEquals(BODY_CLOSE_ELEMENT))
						{
							line = doRegularExpressionReplacements(line);
							sb.append(line);
							line = fileIn.nextLine();
						}
						int endIndex = line.indexOf(BODY_CLOSE_ELEMENT);
						sb.append(line.substring(0, endIndex));
						
						state = State.LOOKING_FOR_TOPIC;
					}
					break;
				}
			}
		}
		
		for(String topic : mapTopicsToPrintWriters.keySet())
		{
			mapTopicsToPrintWriters.get(topic).close();
		}
	}
	
	private static String doRegularExpressionReplacements(String line)
	{
		line = line.replaceAll("&#[0-9]*;", "");
		line = line.replaceAll("&lt;", "<");
		return line;
	}

	private static Map<String, PrintWriter> getMapToTopicFiles() throws IOException
	{
		Scanner fileIn = new Scanner(new File(PATH_TO_ALL_TOPICS_FILE));
		
		Map<String, PrintWriter> mapTopicsToFiles = new TreeMap<String, PrintWriter>();
		
		while(fileIn.hasNextLine())
		{
			String topic = fileIn.nextLine();
			PrintWriter topicPW = new PrintWriter(new BufferedWriter(new FileWriter(PATH_TO_NEW_FILES + topic)));
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
}
