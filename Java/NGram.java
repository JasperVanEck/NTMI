/* Auteurs: Floris de Bruin(5772583), Gijs van Horn(10070370) en Jasper van Eck(6228194)
 * NGram maakt allemaal N-Grams van een String en stopt deze in een HashMap. Waarna ze met
 * een TreeMap gesorteerd kunnen worden, en tevens ook geprint.
 */

import java.util.*;
import java.util.regex.*;
import java.io.*;


public class NGram{
	
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	TreeMap<String, Integer> sortedMap;
	FileManager manager;
	int nGramSize;
	int m;

	/*
	Constructor voor nGram, specificeer welke corpus gebruikt wordt en welke orde n-grams berekend worden.
	*/
	public NGram(String inputFile, int n) {
		nGramSize = n;
		
		String inputFileName = inputFile.replaceFirst("\\.txt", "");
		String outputFile = inputFileName + "_" + n + "grams.txt";
				
		this.manager = new FileManager(inputFile, outputFile);
		computeNGrams2();
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
	computeNGrams krijgt van de manager een string met alle tekst. Deze wordt woord voor woord afgelopen, 
	grams van gewenst formaat gemaakt en in de hashmap opgeslagen.
	*/
	public void computeNGrams(){
		Pattern splitPoint = Pattern.compile(" ");
		
		String text = manager.readWholeFile();
		String nGram = "";
		
		String[] words = splitPoint.split(text);
		
		int lineSize = words.length - (nGramSize);
		
		for(int i=0; i < lineSize; i++){
			nGram = words[i];
			for(int j=1; j < nGramSize; j++){
				nGram = nGram + " " + words[i+j];
			}
			addToMap(nGram);
		}

	}
	
	/*
	computeNGrams2 leest van de manager regel voor regel de gegeven file uit. De woorden die meerdere malen worden gebruikt, worden tijdelijk
	opgeslagen. Hierdoor is het mogelijk om het bestand regel voor regel uit te lezen, in plaats van het hele bestand in 1 haal uit te lezen.
	Verder werkt computeNGrams2 het zelfde als computeNGrams.
	 */
	public void computeNGrams2(){
		Pattern splitPoint = Pattern.compile(" ");
		String[] pastWords = new String[nGramSize - 1];
		String nextLine = manager.readNextLine2();
		String nGram = "";
		boolean firstRun = true;
		
		
		while(nextLine != null){
		
			if (nextLine.matches(".*\\w.*")){
				nextLine = nextLine.trim().replaceAll(" +"," ");
				

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
				
				for(int i=0; i <= lineSize; i++){
					nGram = words[i];
					//System.out.println(words[i]);
					for(int j=1; j < nGramSize; j++){
						nGram = nGram + " " + words[i+j];
						//System.out.println(words[i+j]);
					}
					addToMap(nGram);
					lastValueOfWords = i;
					//System.out.println(nGram);
				}
				for (int ii=0; ii < nGramSize -1; ii++){
					lastValueOfWords++;
					try{
						pastWords[ii] = words[lastValueOfWords];
					} catch (Exception e){
						System.out.println("Ngram size too big for this sentence:");
						System.out.println(Arrays.toString(words) + "\n");
					}
				}
				nextLine = manager.readNextLine2();
			}else{
				nextLine = manager.readNextLine2();
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
}

