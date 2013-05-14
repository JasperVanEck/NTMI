
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

	public static void main(String[] args){
		Smoothing smooth = new Smoothing("austen.txt", false);
		
	}
	
	public Smoothing(String corpus, boolean pos){

				startTime = System.currentTimeMillis();
		
		NGram analyzer = new NGram(corpus, 3, pos);
		nGrams = analyzer.getHashMap();
		startSymbolCount = analyzer.getStartSymbolCount();
		//writeToFile(nGrams, "nGrams.txt");
		
		NGram analyzerMinOne = new NGram(corpus, 2, pos);
		nGramsMinOne = analyzerMinOne.getHashMap();
		
				endTime   = System.currentTimeMillis();
				time = endTime - startTime;
				System.out.println("Create nGrams time: " + time);
		
				startTime = System.currentTimeMillis();
		goodTuring(4);
				endTime   = System.currentTimeMillis();
				time = endTime - startTime;
				System.out.println("Good Turing Smoothing Calculation time: " + time);
	
		//writeToFile(nGramsMinOne, "nGramsMinOne.txt");
		/*
				startTime = System.currentTimeMillis();
		addOneSmoothing();
				endTime   = System.currentTimeMillis();
				time = endTime - startTime;
				System.out.println("Add-One Smoothing Calculation time: " + time);
		*/
		//writeToFile(nGramsAddOnePoss, "smoothed.txt");
		
	}
	
	public void addOneSmoothing(){	
	
		for (Map.Entry<String,Integer> entry : nGrams.entrySet()){
			//get first word of bigram by splitting at spaces and returning the first entry of the array crated by the split.
			String prefix = entry.getKey().split("\\s+")[0];
			//System.out.println(entry.getKey());
			//System.out.println(prefix);
			double nMinOneCount = nGramsMinOne.size();
			double prefixCount;
			
			if(prefix.equals("null")){
				continue;
			}
			
			if(prefix.equals("<s>"))
			{
				prefixCount = startSymbolCount;
			} else {
				prefixCount = nGramsMinOne.get(prefix);
			}
			
			double underDivider = nMinOneCount + prefixCount;
			double aboveDivider = 1;
			
			if(nGrams.containsKey(entry.getKey())){
				aboveDivider = nGrams.get(entry.getKey()) + 1;
			}
			
			double poss = aboveDivider / underDivider;			
			
			this.nGramsAddOnePoss.put(entry.getKey(), poss);
		}
		
	}
	
	public void goodTuring(int k) {
		
		int[] counts = getNCounts(k);
		double[] adjustedCounts = new double[k+1];
		//System.out.println("Original counts: " + Arrays.toString(counts));
		
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
			String prefix = bigram.split("\\s+")[0];
			
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
		
		this.nGramsGoodTuringPoss.put("dummy", adjustedCounts[0]/(double)nGrams.size());
		
		//writeToFile(this.nGramsGoodTuringPoss, "GoodTuringPoss.txt");
		
	}
	
	public void smoothedTagsWords(){
	
	
	}
	
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
	
	public int getCountFreq(int freq){
		return 0;
	
	}
	
	public double getAddOnePoss(String bigram){
		if(nGramsAddOnePoss.containsKey(bigram)){	
			return nGramsAddOnePoss.get(bigram);
		}else{
			String prefix = bigram.split("\\s+")[0];
			if(nGramsMinOne.containsKey(prefix)){
				return 1/ (nGramsMinOne.size() + nGramsMinOne.get(prefix));
			}else{
				return 1/ (nGramsMinOne.size()	+ 1);
			}
		}
	}
	
	
	public double getGoodTuringPoss(String bigram){
		//System.out.println(bigram);
		if(this.nGramsGoodTuringPoss.containsKey(bigram)){
			return this.nGramsGoodTuringPoss.get(bigram);
		}else{
			return this.nGramsGoodTuringPoss.get("dummy");
		}	
	}
	
	
	private double missingMass(int n1, int n) {
		double mass = (double) n1 / (double) n;
		return mass;
	}
	
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

