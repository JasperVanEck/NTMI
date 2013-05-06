
import java.util.*;
import java.io.*;
import java.util.regex.*;


public class Smoothing {

	HashMap<String, Integer> nGrams;
	HashMap<String, Integer> nGramsMinOne;
	HashMap<String, Double> nGramsAddOnePoss = new HashMap<String, Double>();
	double bigN, startSymbolCount;
	long startTime, endTime, time;

	public static void main(String[] args){
		Smoothing smooth = new Smoothing("austen.txt");
	}
	
	public Smoothing(String corpus){

				startTime = System.currentTimeMillis();
		
		NGram analyzer = new NGram(corpus, 2);
		nGrams = analyzer.getHashMap();
		
		writeToFile(nGrams, "nGrams.txt");
		
		NGram analyzerMinOne = new NGram(corpus, 1);
		nGramsMinOne = analyzerMinOne.getHashMap();
		
		writeToFile(nGramsMinOne, "nGramsMinOne.txt");
		
				endTime   = System.currentTimeMillis();
				time = endTime - startTime;
				System.out.println("Create nGrams time: " + time);
		
				startTime = System.currentTimeMillis();
		addOneSmoothing();
				endTime   = System.currentTimeMillis();
				time = endTime - startTime;
				System.out.println("Smoothing Calculation: " + time);
		
		writeToFile(nGramsAddOnePoss, "smoothed.txt");
		
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
			
			nGramsAddOnePoss.put(entry.getKey(), poss);
		}
	}
	/**
	public double findOccurences(String prefix){
		double totalOccurences = 0;
		for (Map.Entry<String,Integer> entry : nGrams.entrySet()){
			if (entry.getKey().contains(prefix + " ")){
				totalOccurences += entry.getValue() + 1;
			}
		}
		return totalOccurences;
	}
	**/
	
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

