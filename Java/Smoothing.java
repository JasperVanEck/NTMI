
import java.util.*;
import java.io.*;
import java.util.regex.*;


public class Smoothing {

	HashMap<String, Integer> nGrams;
	HashMap<String, Integer> nGramsMinOne;
	HashMap<String, Double> nGramsAddOnePoss = new HashMap<String, Double>();
	HashMap<String, Double> nGramsGoodTuringPoss = new HashMap<String, Double>();
	double bigN, startSymbolCount;
	long startTime, endTime, time;
	
	/**
	Empty constructor
	**/
	public Smoothing(){
	
	}
	
	/**
	goodTuringPos performs Good-Turing smoothing over map posNGrams, using posNMinusOneGrams, k (the maximum counts that should be smoothed) and sentenceCount, the total amount of sentences in the 
	training corpus. It iterates over all entries in posNGrams, finds the corresponding nGram in posNMinusOneGrams and adjusts the count accordingly. 
	**/
	public HashMap<String, Double> goodTuringPos(HashMap<String, Integer> posNGrams, 
												HashMap<String, Integer> posNMinusOneGrams, 
												int k, int sentencesCount) {
		
		int[] counts = getNCounts2(posNGrams, k);
		double[] adjustedCounts = getAdjustedCounts(counts, posNGrams.size(), posNMinusOneGrams.size(), k);
		HashMap<String, Double> nGramsGoodTuringPoss = new HashMap<String, Double>();
		
		for(Map.Entry<String,Integer> entry : posNGrams.entrySet()){
		
			double poss;
			int count = entry.getValue();
			double nMinusOneGramCount;
			double adjustedCount = (double)count;
			String nGram = entry.getKey();
			
			//Create an (n-1)gram given the nGram by splitting the string to an array and filling a new string with the first (n-1) elements.
			String[] nGramsPosArray = nGram.split("\\s+");
			String[] nMinusOnePosArray = Arrays.copyOfRange(nGramsPosArray, 0, nGramsPosArray.length-1);
			String prefix = "";
			for(String elem : nMinusOnePosArray){
				prefix+=elem + " ";
			}
			prefix = prefix.substring(0, prefix.length() - 1);
			
			//Adjust the sentencecounter, recognized by startsymbols.
			if(prefix.matches("^[<s>\\s*]+")){
				nMinusOneGramCount = sentencesCount;
			} else {
				nMinusOneGramCount = posNMinusOneGrams.get(prefix);
			}
			
			//set adjustedcount if necessary
			if(count <= k) {
				adjustedCount = adjustedCounts[count];
			}
			
			poss = adjustedCount/nMinusOneGramCount;
			
			nGramsGoodTuringPoss.put(nGram, poss);
 		}
		//add dummy which accounts for unknown mass.
		nGramsGoodTuringPoss.put("dummy", adjustedCounts[0]/(double)posNGrams.size());
		
		return nGramsGoodTuringPoss;
	}
	
	
	/**
	getNCounts2 is a utility function that returns the amount of nGrams in map posNGrams with count up to and including k.
	**/
	private int[] getNCounts2(HashMap<String, Integer> posNGrams, int k){
		int[] results = new int[k+1];
		for(Map.Entry<String,Integer> entry : posNGrams.entrySet()){
			int value = entry.getValue();
			if(value <= (k + 1)){
				results[value-1]++;
			}
		}
		return results;
	}
	
	/**
	goodTuringPosTagsCalcPossibilities recieves a Map<String, Map<String, Integer>> posTagDictionary in which for every POS-tag a map with words and counts exists. 
	
	It iterates over that map and performs per tag:
	- Good-Turing smoothing over the words in the submap
	- Possibility calculations that give all possibilities P(w|t), chance of word given the tag.
	
	It returns a map with <tag, nestedmap> pairs in which the posibilities of words given the tag are given. 
	A new word is added to every map: 0Count, which is the reserved mass for unknown words.
	**/
	public HashMap<String, HashMap<String, Double>> goodTuringPosTagsCalcPossibilities(
																	Map<String, Map<String, Integer>> posTagDictionary){
		
		HashMap<String, HashMap<String, Double>> tagWithWordsPossMap = new HashMap<String, HashMap<String, Double>>();
		
		
		double newValue = 0;
		double nOneCount = 0;
		double totalWordCountPerTag = 0;
		double possibility = 0;
		for(Map.Entry<String, Map<String, Integer>> elem : posTagDictionary.entrySet()){
			HashMap<String, Double> wordsWithNewCount = new HashMap<String, Double>();
			HashMap<String, Double> wordsWithPossibility = new HashMap<String, Double>();
					
			//This loop performs calculation of new counts according to Good-Turing.
			for(Map.Entry<String, Integer> elem2 : elem.getValue().entrySet()) {
				newValue = (double)elem2.getValue();
				totalWordCountPerTag += newValue;
				
				if(newValue == 1){
					newValue = 0.5;
					nOneCount++;
				}
				wordsWithNewCount.put(elem2.getKey(),newValue);
			
			}
			double missingCount = 0.5 * (nOneCount/totalWordCountPerTag);
			wordsWithNewCount.put("0Count", missingCount);
			
			//This loop performs calculation of possibilities P(w|t).
			for(Map.Entry<String, Double> elem2 : wordsWithNewCount.entrySet()){
				possibility = elem2.getValue()/totalWordCountPerTag;
				wordsWithPossibility.put(elem2.getKey(), possibility);
			}
			
			//put the new map of words and their possibilities back.
			tagWithWordsPossMap.put(elem.getKey(), wordsWithPossibility);
			
			nOneCount = 0;
			totalWordCountPerTag = 0;
			
		}		
		return tagWithWordsPossMap;
	}
	
	/**
	Separate function that calculates adjusted counts for an array of ints, returns a double array. Smoothes according to Good-Turing.
	**/
	private double[] getAdjustedCounts(int[] counts, int sizeNGramMap, int sizeNMinusOneGramMap, int k){
		
		double[] adjustedCounts = new double[k+1];		
		double countNextK = (double)counts[k];
		double nZero = (double)sizeNMinusOneGramMap*sizeNMinusOneGramMap-sizeNGramMap;
		double countR;
		
		//Iterates over the counts[] array and adjusts the counts according to Good-Turing.
		for(int r = 0; r <= k; r++){
			double countNextR = (double)counts[r];
			
			if (r == 0){
				countR = nZero;
			}else{
				countR = (double)counts[r-1];
			}	
			double countOne = (double)counts[0];
			
			double aboveDivide = (r + 1) * (countNextR/countR) - r*(((k+1)*countNextK)/countOne);
			double belowDivide = 1 - (((k+1)*countNextK)/countOne);
		
			adjustedCounts[r] = aboveDivide/belowDivide;
			
		}
		return adjustedCounts;
	}
	
	/**
	performs Good-Turing smoothing over the local nGrams, used for building the language model.
	**/
	public void goodTuring(int k) {
		
		int[] counts = getNCounts(k);
		double[] adjustedCounts = new double[k+1];
		
		double countNextK = (double)counts[k];
		double nZero = (double)nGramsMinOne.size()*nGramsMinOne.size()-nGrams.size();
		double countR;
		
		//Adjust counts for Frequencies under or equal to k.
		for(int r = 0; r <= k; r++){
			double countNextR = (double)counts[r];
			
			if (r == 0){
				countR = nZero;
			}else{
				countR = (double)counts[r-1];
			}	
			double countOne = (double)counts[0];
			
			double aboveDivide = (r + 1) * (countNextR/countR) - r*(((k+1)*countNextK)/countOne);
			double belowDivide = 1 - (((k+1)*countNextK)/countOne);
		
			adjustedCounts[r] = aboveDivide/belowDivide;
			
		
		}
		
		this.nGramsGoodTuringPoss = new HashMap<String, Double>();
		
		//fill the hashmap with conditional probabilities for each bigram.
		for(Map.Entry<String,Integer> entry : nGrams.entrySet()){
			double poss;
			int count = entry.getValue();
			double unigramCount;
			double adjustedCount = (double)count;
			String bigram = entry.getKey();
			String prefix = bigram.split("\\s+")[0] + " " + bigram.split("\\s+")[1];
			
			if(prefix.equals("null")){
				continue;
			}
			if(prefix.equals("<s>")){
				unigramCount = startSymbolCount;
			}else{
				unigramCount = nGramsMinOne.get(prefix);
			}
			
			if(count <= k){
				adjustedCount = (double)adjustedCounts[count];
			}
			
			poss = adjustedCount/unigramCount;
			this.nGramsGoodTuringPoss.put(bigram, poss);
		
		}
		//put dummy variable in map for unknown mass.
		this.nGramsGoodTuringPoss.put("dummy", adjustedCounts[0]/(double)nGrams.size());		
	}
	
	/**
	getNCounts returns an int[] with counts for how many map entries in nGrams have count up to and including k.
	**/
	public int[] getNCounts(int k){
		int[] results = new int[k+1];
		for(Map.Entry<String,Integer> entry : nGrams.entrySet()){
			int value = entry.getValue();
			if (value <= (k+1)){
				results[value-1]++;
			}
		}
		return results;	
	}
	
	/**
	Returns the good-turing possibility for string bigram, if bigram is not found, returns the value associated with dummy, the missing mass probability.
	**/
	public double getGoodTuringPoss(String bigram){
		//System.out.println(bigram);
		if(this.nGramsGoodTuringPoss.containsKey(bigram)){
			return this.nGramsGoodTuringPoss.get(bigram);
		}else{
			return this.nGramsGoodTuringPoss.get("dummy");
		}	
	}
	
	/**
	Writes HashMap map to file filename.txt
	---- obsolete ----
	**/
	private void writeToFile(HashMap map, String fileName){
		Iterator it = map.entrySet().iterator();
		try{
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);

			while(it.hasNext()){
				Map.Entry pairs = (Map.Entry)it.next();
				out.write(pairs.getValue() + "\t\t - \t\t" + pairs.getKey() + "\n");
			}
			out.close();
		} catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}

