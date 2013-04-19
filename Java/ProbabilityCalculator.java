
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class ProbabilityCalculator{

	private NGram[] nGrams;
	private FileManager manager;
	private int n;
	private TreeMap<Double, String> sortedSentences = new TreeMap<Double, String>();

	public ProbabilityCalculator(String addFile, String corpusFile, int n){
		this.n = n;
		
		this.nGrams = new NGram[2];
		this.nGrams[0] = new NGram(corpusFile, n);
		this.nGrams[1] = new NGram(corpusFile, n - 1);
		
		this.manager = new FileManager(addFile);
		
	}
	
	public void calculate(){
	
		Pattern splitPoint = Pattern.compile(" ");
		String nextLine = this.manager.readNextLine();
				
		while(nextLine != null){
		
			String[] words = splitPoint.split(nextLine);
			
			int wordsLength = words.length;
			
			if(wordsLength == this.n){
				String[] wordsMinOne = new String[words.length-1];
				wordsMinOne = Arrays.copyOfRange(words, 0, words.length-1);
				
				String sentence = ("" + Arrays.asList(words)).replaceAll("(^.|.$)", "").replace(", ", " ");
				String shortSentence = ("" + Arrays.asList(wordsMinOne)).replaceAll("(^.|.$)", "").replace(", ", " ");;
				
				double freq1 = nGrams[0].getValue(sentence);
				double freq2 = nGrams[1].getValue(shortSentence);
				
				System.out.printf("Given '%s' the chance for '%s' is: %.10f \n", shortSentence, sentence, freq1/freq2);
				
			}
			
			nextLine = this.manager.readNextLine();			
		}
	}
	
	public void calculateArbitrary(){
	
		Pattern splitPoint = Pattern.compile(" ");
		String nextLine = this.manager.readNextLine();			
		
		while(nextLine != null){
			//nextLine = nextLine + " </s>";
			for (int i = 1; i < this.n; i++){
				nextLine = "<s> " + nextLine;
			}
			double probability = 1;
			//System.out.println(nextLine);
			String[] words = splitPoint.split(nextLine);
			if(words.length - this.n > this.n){
				
				boolean firstrun = true;
							
				for(int i=this.n; i < words.length; i++ ){
					String[] tempGramMinOne = Arrays.copyOfRange(words, i-this.n + 1, i);
					String[] tempGram = Arrays.copyOfRange(words, i-this.n + 1, i+1);
					
					String shortSentence = ("" + Arrays.asList(tempGramMinOne)).replaceAll("(^.|.$)", "").replace(", ", " ");
					String sentence = ("" + Arrays.asList(tempGram)).replaceAll("(^.|.$)", "").replace(", ", " ");
					
					//System.out.println("shortsentence is: " + shortSentence);
					//System.out.println("sentence is: " + sentence);
					
					
					double freq2;
					double freq1;
					
					try{
						freq1 = nGrams[0].getValue(sentence);
						if(firstrun){
							 freq2 = nGrams[1].getTotalSentences();
							 firstrun = false;
						}else{
							freq2 = nGrams[1].getValue(shortSentence);
						}
					} catch(Exception e){
						freq1 = 0;
						freq2 = 1;
					}
					
					probability = probability * (freq1/freq2);
					//System.out.println(probability);
					
					
				}
				//System.out.printf("The probability for sentence: '%s' is: %e \n", nextLine, probability);
				addToMap(nextLine, probability);
				nextLine = this.manager.readNextLine();
			}else{
				//System.out.println("The sentence was too short for the ngram size");
				nextLine = this.manager.readNextLine();
			}
		
		}
	}
	
	public void calculateArbitraryAddOneSmoothed(){
	
		Pattern splitPoint = Pattern.compile(" ");
		String nextLine = this.manager.readNextLine();			
		
		while(nextLine != null){
			//nextLine = nextLine + " </s>";
			for (int i = 1; i < this.n; i++){
				nextLine = "<s> " + nextLine;
			}
			double probability = 1;
			//System.out.println(nextLine);
			String[] words = splitPoint.split(nextLine);
			
			if(words.length - this.n >= this.n){
				
				boolean firstrun = true;
				//System.out.println("Ik ben echt bezig!");			
				for(int i=this.n; i < words.length; i++ ){
					String[] tempGramMinOne = Arrays.copyOfRange(words, i-this.n + 1, i);
					String[] tempGram = Arrays.copyOfRange(words, i-this.n + 1, i+1);
					
					String shortSentence = ("" + Arrays.asList(tempGramMinOne)).replaceAll("(^.|.$)", "").replace(", ", " ");
					String sentence = ("" + Arrays.asList(tempGram)).replaceAll("(^.|.$)", "").replace(", ", " ");
					
					//System.out.println("shortsentence is: " + shortSentence);
					//System.out.println("sentence is: " + sentence);
					
					
					double freq2 = 625850//this.nGrams[0].countPrefix(shortSentence);
					System.out.printf("Freq2 voor de prefix %s is %f \n", shortSentence, freq2);
					double freq1 = 1;
					
					if(this.nGrams[0].containsKey(sentence)){
						freq1 += this.nGrams[0].getValue(sentence);
					}
					
					if(firstrun){
						 freq2 += this.nGrams[1].getTotalSentences();
						 firstrun = false;
					}else{
						if(this.nGrams[1].containsKey(shortSentence)){
							freq2 += this.nGrams[1].getValue(shortSentence);
						}
					}
					System.out.println("freq2 is" + freq2);
					probability = probability * (freq1/freq2);
					//System.out.println(probability);
					
					
				}
				System.out.printf("The probability for sentence: '%s' is: %e \n", nextLine, probability);
				addToMap(nextLine, probability);
				nextLine = this.manager.readNextLine();
			}else{
				//System.out.println("The sentence was too short for the ngram size");
				nextLine = this.manager.readNextLine();
			}
		
		}
	}
	
	
	
	public void addToMap(String sentence, double prob){
		sortedSentences.put(prob, sentence);
	}
	
	
	public void printTopTwoProbabilities(){
	
		NavigableMap<Double, String> reversed = sortedSentences.descendingMap();
	
		int i=0;
		
		Iterator entries = reversed.entrySet().iterator();
		while(i < 2 && entries.hasNext()){
			Map.Entry entry = (Map.Entry) entries.next();
			double prob = new Double(entry.getKey().toString());
			if(prob > 0.0){
				System.out.printf("Probability for: '%s' is: %e \n", entry.getValue(), entry.getKey());
			}else{
				System.out.println("The probabilities for (the rest of) the sentences are zero");
			}
			i++;
		}
	}
	
}

