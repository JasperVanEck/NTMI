/* Auteurs: Floris de Bruin(5772583), Gijs van Horn(10070370) en Jasper van Eck(6228194)
 * NGram maakt allemaal N-Grams van een String en stopt deze in een HashMap. Waarna ze met
 * een TreeMap gesorteerd kunnen worden, en tevens ook geprint.
 */

import java.util.*;
import java.util.regex.*;
import java.io.*;


public class NGram{
	
	private HashMap<String, Integer> map = new HashMap<String, Integer>();
	private TreeMap<String, Integer> sortedMap;
	private FileManager manager;
	private int nGramSize;
	private int m;
	private int sentences;

	/*
	Constructor voor nGram, specificeer welke corpus gebruikt wordt en welke orde n-grams berekend worden.
	*/
	public NGram(String inputFile, int n) {
		nGramSize = n;
		this.sentences = 0;
		String inputFileName = inputFile.replaceFirst("\\.txt", "");
		String outputFile = inputFileName + "_" + n + "grams.txt";
				
		this.manager = new FileManager(inputFile, outputFile);
		computeNGramsWithDummySymbols();
		sortMap();
		
		
	}
	
	/*
	Overloaded constructor, voor het specificeren hoeveel van de meest voorkomende nGrams geprint moeten worden.
	*/
	public NGram(String inputFile, int n, int m){
		
		this(inputFile, n);
		this.m = m;
		//printSumFrequencies();
	}
	
	
	/*
	computeNGramsWithDummySymbols leest van de manager regel voor regel de gegeven file uit. De woorden die meerdere malen worden gebruikt, worden tijdelijk
	opgeslagen. Hierdoor is het mogelijk om het bestand regel voor regel uit te lezen, in plaats van het hele bestand in 1 haal uit te lezen.
	Verder werkt computeNGramsWithDummySymbols het zelfde als computeNGrams.
	 */
	public void computeNGramsWithDummySymbols(){
		Pattern splitPoint = Pattern.compile(" ");
		String[] pastWords = new String[nGramSize - 1];
		String nextLine = manager.readNextLineWithDummySymbols();
		String nGram = "";
		boolean firstRun = true;
		
		String startsymbols = "";
		for(int i=1; i < nGramSize; i++){
			startsymbols = startsymbols + "<s> ";
		}
		
		while(nextLine != null){
		
			if (nextLine.matches(".*\\w.*")){
				nextLine = nextLine.trim().replaceAll(" +"," ");				
				nextLine = nextLine.replace("<s> ", startsymbols);
				if(nextLine.contains("</s>")){
					this.sentences++;
				}

				String[] currentWords = splitPoint.split(nextLine);
				
				
				String[] words = new String[currentWords.length + pastWords.length];
				
				if(!firstRun){
					int k = 0;
					for(String elem: pastWords){
						words[k] = elem;
						k++;
					}
					for(String elem: currentWords){
						words[k] = elem;
						k++;
					}
				}else{
					words = currentWords;
					firstRun = false;
				}
				
				int lineSize = words.length - (nGramSize);
				
				
				int lastValueOfWords = 0;

				for(int i = 0; i <= lineSize; i++){
					nGram = words[i];
					String lastWord = "";

					//System.out.println(words[i]);
					for(int j = 1; j < nGramSize; j++){
						lastWord = words[i + j];
						nGram = nGram + " " + lastWord;
						//System.out.println(words[i+j]);
					}
					addToMap(nGram);
				}

				if(Arrays.asList(words).contains("</s>")){
					lastValueOfWords = lineSize + nGramSize - 1;
				}else{
					lastValueOfWords = lineSize;
				}

				for (int ii=0; ii < nGramSize -1; ii++){
					lastValueOfWords++;
					try{
						pastWords[ii] = words[lastValueOfWords];
					} catch (Exception e){
						//System.out.println("Ngram size too big for this sentence:");
						//System.out.println(Arrays.toString(words) + "\n");
					}
				}
				nextLine = manager.readNextLineWithDummySymbols();
			}else{
				nextLine = manager.readNextLineWithDummySymbols();
			}
		}

	}
	
	/*
	Voegt string nGram toe aan de HashMap, als deze al voorkomt wordt de value opgehoogd anders nieuw toegevoegd met frequency 1.
	*/
	public void addToMap(String nGram){
		if(map.containsKey(nGram)){
			int currentValue = map.get(nGram);
			map.put(nGram, currentValue+1);
		}else{
			map.put(nGram, 1);
		}
	}
	
	/*
	Sorteert de hashMap map en zet deze in TreeMap sortedMap. 
	*/
	public void sortMap(){
		
		FrequencyComparator comparator = new FrequencyComparator(this.map);
		sortedMap = new TreeMap<String, Integer>(comparator);
		sortedMap.putAll(this.map);
	
	}
	
	public int getN(){
		return this.nGramSize;
	}

	/*
	Print de m meest voorkomende NGrams uit.
	*/
	public void printTopFrequencies(){
		int i=0;
		Iterator entries = sortedMap.entrySet().iterator();
		while(i < m && entries.hasNext()){
			Map.Entry nGram = (Map.Entry) entries.next();
			System.out.println(nGram.getKey() + " - " + nGram.getValue());
			i++;
		}
	}
	
	/*
	Print de totale som van alle frequenties.
	*/
	public void printSumFrequencies(){
		Integer sum = 0;
		for(Map.Entry<String, Integer> entry : sortedMap.entrySet()){
			sum = sum + entry.getValue();
		}
		System.out.println("Total of all frequencies: " + sum);
	}

	public void writeTopFrequencies(){
		int i = 0;
		Iterator entries = sortedMap.entrySet().iterator();
		while(i < 25 && entries.hasNext()){
			Map.Entry nGram = (Map.Entry) entries.next();
			this.manager.writeToFile((String) nGram.getKey());
			i++;
		}
		try{
			this.manager.terminate();
		}catch(IOException e){
			System.out.println("Couldn't close file");
		}
	}

	public void terminate(){
		try{
			this.manager.terminate();
		}catch(IOException e){
			System.out.println("Couldn't close file.");
		}
	}
	
	public int getValue(String key){
			return this.map.get(key);			
	}
	
	public int getTotalSentences(){
			return this.sentences;
	}
	
}

