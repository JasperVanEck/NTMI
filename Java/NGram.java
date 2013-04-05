
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class NGram{
	
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	TreeMap<String, Integer> sortedMap;
	FileManager manager;
	int nGramSize;

	public NGram(String inputFile, int n) {
		nGramSize = n;
		
		String inputFileName = inputFile.replaceFirst("\\.txt", "");
		String outputFile = inputFileName + "_" + n + "grams.txt";
				
		this.manager = new FileManager(inputFile, outputFile);
		
		computeNGrams();
		
		sortMap(map);
		

	}
	
	public NGram(String inputFile, int n, int m){
		
		this(inputFile, n);
		printTopFrequencies(m);
	}
	
	public void computeNGrams(){
		Pattern splitPoint = Pattern.compile(" ");
		
		String nextLine = manager.readNextLine();
		String nGram = "";
		
		String[] words;
		
		while(nextLine != null){
			words = splitPoint.split(nextLine);
			int lineSize = words.length - (nGramSize-1);
			
			for(int i=0; i < lineSize; i++){
				nGram = words[i];
				for(int j=1; j < nGramSize; j++){
					nGram = nGram + " " + words[i+j];
				}
				addToMap(nGram);
				//System.out.println(nGram);
			}
			nextLine = manager.readNextLine();
		}
	}
	
	public void addToMap(String nGram){
		if(map.containsKey(nGram)){
			int currentValue = map.get(nGram);
			map.put(nGram, currentValue+1);
		}else{
			map.put(nGram, 1);
		}
	}
	
	public void sortMap(HashMap<String, Integer> map){
		
		FrequencyComparator comparator = new FrequencyComparator(map);
		sortedMap = new TreeMap<String, Integer>(comparator);
		sortedMap.putAll(map);
	
	}
	
	public void printTopFrequencies(int m){
		int i=0;
		Iterator entries = sortedMap.entrySet().iterator();
		while(i < m && entries.hasNext()){
			Map.Entry nGram = (Map.Entry) entries.next();
			System.out.println(nGram.getKey() + " - " + nGram.getValue());
			i++;
		}
	}
		
	
}

