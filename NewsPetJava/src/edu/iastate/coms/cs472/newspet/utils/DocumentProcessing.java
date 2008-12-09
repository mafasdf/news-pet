package edu.iastate.coms.cs472.newspet.utils;

import java.util.ArrayList;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;

public class DocumentProcessing
{
	public static Pipe createConversionPipes()
	{
		ArrayList<Pipe> pipesArr = new ArrayList<Pipe>();
		
		//tokenize
		pipesArr.add(new CharSequence2TokenSequence());//TODO pass in pattern
		//make lowercase
		pipesArr.add(new TokenSequenceLowercase());
		// conversionPipes.add(new TokenSequenceRemoveStopwords(false, false));
		// convert words to integers
		pipesArr.add(new TokenSequence2FeatureSequence());
		
		//TODO: needed? is there a more efficient workaround?
		//convert category ints to ints
		pipesArr.add(new Target2Label());
		
		//TODOpipesArr.add(new TokenSequence2FeatureSequence());
		//convert token integer set to a sparse vector
		pipesArr.add(new FeatureSequence2FeatureVector());
		
		return new SerialPipes(pipesArr);
	}
}
