import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Scanner;

public class DemonstrateProblem
{
	public static void main(String[] args) throws IOException
	{
		String fileName = "reut2-017.sgm";
		
		example1(fileName);
		
//		example2(fileName);
	}
	
	private static void example1(String fileName) throws FileNotFoundException
	{
		Scanner fileIn = new Scanner(new File(fileName));
		
		String line = null;
		int i;
		for(i = 0; fileIn.hasNextLine(); i++)
		{
			line = fileIn.nextLine();
		}
		System.out.println("The Scanner class says that the last line is on line number " + i + " and is the following:");
		System.out.println(line);
		
		System.out.println();
		
		System.out.println("By manual inspection, this file contains 36256 lines and the last line is the following:");
		System.out.println("</REUTERS>");
		
		System.out.println();
		
		System.out.println("Furthermore, manual inspection says that line 35573 is the following:");
		System.out.println("economic summit in Venice on June 8-10, U.K. Chancellor of the");
	}
	
	/*
	 * I am trying a different approach just to see what will happen, but I do
	 * not have the details correct yet.
	 */
	private static void example2(String fileName) throws FileNotFoundException, IOException
	{
		FileReader fr = new FileReader(fileName);
		CharBuffer cb = CharBuffer.allocate(25);
		String line = null;
		for(int retVal = 0; retVal != -1;)
		{
			if(fr.ready())
			{
				retVal = fr.read(cb);
				cb.rewind();
			}
			line = cb.toString();
		}
		System.out.println(line);
	}
}
