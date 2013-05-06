
import java.util.*;
import java.io.*;
import java.util.regex.*;


public class Smoothing {

	TreeMap<String, Integer> NGrams;
	TreeMap<String, Integer> NGramsAddOne = new TreeMap<String, Integer>();
	TreeMap<String, Double> NGramsSmoothed = new TreeMap<String, Double>();
	double bigN;

	public static void main(String[] args){
		Smoothing smooth = new Smoothing("austen.txt");
	}
	
	public Smoothing(String corpus){
		NGram analyzer = new NGram(corpus, 2);
		NGrams = analyzer.getSortedMap();
		writeToFile();
		bigN = analyzer.getBigN();
		//System.out.println(bigN);
		//addOneSmoothing();		
	}
	
	public void addOneSmoothing(){	
		for (Map.Entry<String,Integer> entry : NGrams.entrySet()){
			NGramsAddOne.put(entry.getKey(),entry.getValue() + 1);
		}
		
		for (Map.Entry<String,Integer> entry : NGrams.entrySet()){
			//get first word of bigram by splitting at spaces and returning the first entry of the array crated by the split.
			String prefix = entry.getKey().split("\\s+")[0];
			double occurences = findOccurences(prefix);
			double valueOfKey = 1;
			
			if(NGramsAddOne.containsKey(entry.getKey())){
				valueOfKey = NGramsAddOne.get(entry.getKey());
			}
			double smoothedValue = valueOfKey / occurences;
			NGramsSmoothed.put(entry.getKey(), smoothedValue);
		}
		System.out.println(NGramsSmoothed);
	}
	
	public double findOccurences(String prefix){
		double totalOccurences = 0;
		for (Map.Entry<String,Integer> entry : NGramsAddOne.entrySet()){
			if (entry.getKey().contains(prefix + " ")){
				totalOccurences += entry.getValue();
			}
		}
		return totalOccurences;
	}
	
	public int goodTuring(int r, int k) {
		/**
		float aboveDevide = (r + 1) * (countNextR/countR) - r*(((k+1)*countNextK)/countOne);
		float belowDevide = 1 - (((k+1)*countNextK)/countOne);
		
		return aboveDevide/belowDevide;
		**/
		
		return 0;
	}
	
	private double missingMass(int n1, int n) {
		double mass = (double) n1 / (double) n;
		return mass;
	}
	
	private void writeToFile(){
		Iterator it = NGrams.entrySet().iterator();
		try{
			FileWriter fstream = new FileWriter("austen_2grams.txt");
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

