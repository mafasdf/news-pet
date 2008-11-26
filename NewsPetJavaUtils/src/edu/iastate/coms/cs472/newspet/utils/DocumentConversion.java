package edu.iastate.coms.cs472.newspet.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.core.SparseInstance;

public class DocumentConversion
{
	/**
	 * Note that the returned instance has no class / category assigned to it.
	 */
	//TODO: rehash 
	public static Instances documentToSingletonInstance(String document, long categoryID)
	{
		//TODO: crop out html (if not done elsewhere)
		//TODO: remove punctuation
		document=document.toLowerCase();
		
		String[] words = document.split("[ \t\f\n\r]*");
		
		//collect and count
		HashMap<String, Integer> wordBag = new HashMap<String, Integer>(words.length);
		for(String word:words)
		{
			if(wordBag.containsKey(word))
				wordBag.put(word, wordBag.get(word)+1);
			else
				wordBag.put(word, 1);
		}
		
		int size=wordBag.size()+1;
		FastVector attributes = new FastVector(size);
		
		int[] indices =new int[size];
		double[] counts=new double[size];
		int i=0;
		for(Map.Entry<String,Integer> pair: wordBag.entrySet())
		{
			indices[i]=i;
			counts[i]=pair.getValue();
			attributes.addElement(new Attribute(pair.getKey()));
			i++;
		}
		//set category/class
		indices[size-1]=size-1;
		counts[size-1]=categoryID;
		
		
		Instances toReturn = new Instances("singletonInstance",attributes, size);
		toReturn.setClassIndex(0);
		
		return toReturn;
		
		
		/*
		//determine indices, store counts to array, and set up vector 
		//TODO: store on DB instead of dangerously using collidable hashcodes
		int[] indices =new int[wordBag.size()];
		double[] counts=new double[wordBag.size()];
		int i=0;
		for(Map.Entry<String,Integer> pair: wordBag.entrySet())
		{
			indices[i]=pair.getKey().hashCode();//TODO
			
			counts[i]=pair.getValue();
			i++;
		}
		*/
		
		//return new SparseInstance(1, counts, indices, indices.length);

		
		/*
		StringTokenizer iter = new StringTokenizer(document);
		
		//TODO: (experimenting) there might be better way
		SparseInstance instance = new SparseInstance(10);
		
		while(iter.hasMoreTokens())
		{
			instance.setValue(iter.nextElement().hashCode(), 1);//TODO: use different string ID system, or different Instance approach 
		}
		
		return instance;*/
	}
}
