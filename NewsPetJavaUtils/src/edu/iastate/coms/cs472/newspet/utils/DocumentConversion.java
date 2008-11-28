package edu.iastate.coms.cs472.newspet.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;

public class DocumentConversion
{
	//TODO: change to DB-stored alphabet
	private static Alphabet alphabet=new Alphabet();
	private static LabelAlphabet alphabetB=new LabelAlphabet();
	
	//filters to tokenize and convert document text 
	private static SerialPipes conversionPipes;
		
	public static Pipe getConversionPipes()
	{
		if(conversionPipes==null)
			generateConversionPipes();
		
		return conversionPipes;
	}

	synchronized private static void generateConversionPipes()
	{
		//check if another thread has been here before
		if(conversionPipes!=null)
			return;
		
		ArrayList<Pipe> pipesArr=new ArrayList<Pipe>();
		
		//tokenize
		pipesArr.add(new CharSequence2TokenSequence());//TODO pass in pattern
		//make lowercase
		pipesArr.add(new TokenSequenceLowercase());
		//TODO conversionPipes.add(new TokenSequenceRemoveStopwords(false, false));
		//convert words to integers with centralized mapping.
		pipesArr.add(new TokenSequence2FeatureSequence(alphabet));
		
		//TODO: needed?
		//convert category ints to ints
		pipesArr.add(new Target2Label(alphabetB));
		
		
		//TODOpipesArr.add(new TokenSequence2FeatureSequence());
		//convert token integer set to a sparse vector
		pipesArr.add(new FeatureSequence2FeatureVector());
		
		conversionPipes=new SerialPipes(pipesArr);
	}
	
	/**
	 * pass in null category for non-training instance.
	 */
	public static Instance generateInstance(String documentText, Long categoryID)
	{
		return getConversionPipes().newIteratorFrom(java.util.Collections.singleton(new Instance(documentText,categoryID, null, null)).iterator()).next();
		//TODO: safety in case of non 1-1 pipe
	}
}
