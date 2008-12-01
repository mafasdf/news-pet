package edu.iastate.coms.cs472.newspet.trainer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import cc.mallet.classify.NaiveBayes;
import edu.iastate.coms.cs472.newspet.utils.ClassifierDAL;

public class QuickTest
{
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//TODO: only a quick test
		
		//try classifying something
		try
		{
			NaiveBayes classifier = ClassifierDAL.getClassifier(456);
			synchronized(classifier)
			{
				PrintWriter pw = new PrintWriter(System.out);
				classifier.classify("fox dogs jumped blah").print(pw);
				classifier.classify("little superman on the prairie").print(pw);
				classifier.classify("SOMETHING COMPLETELY DIFFERENT").print(pw);
				pw.flush();
			}
		}
		catch(FileNotFoundException e)
		{
			throw new RuntimeException("We just lost the game", e);
		}
		
	}
	
}
