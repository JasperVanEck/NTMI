
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class ProbabilityCalculator{

	private NGram[] nGrams;
	private FileManager manager;
	private Smoothing smoother;
	private int n;
	private TreeMap<Double, String> sortedSentences = new TreeMap<Double, String>();

	public ProbabilityCalculator(String addFile, String corpusFile, int n){
		this.n = n;
		
		this.nGrams = new NGram[2];
		this.nGrams[0] = new NGram(corpusFile, n);
		this.nGrams[1] = new NGram(corpusFile, n - 1);
		
		this.manager = new FileManager(addFile);
	}
	
	public ProbabilityCalculator(String testFile){
		this.n = 2;
		this.smoother = new Smoothing("austen.txt");
		this.manager = new FileManager(testFile, "evaluated_sentences.txt");
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
	
	/**
	This function performs probability calculations on sentences which are each one on their own line.
	It writes the unsmoothed, add-one smoothed and Good-Turing smoothed probabilities to the file evaluated_sentences.txt.
	**/
	public void calculateSmoothed(){
	
		Pattern splitPoint = Pattern.compile(" ");
		String nextLine = this.manager.readNextLine();	

		double unSmoothedZeroCounter = 0;
		double addOneZeroCounter = 0;
		double goodTuringZeroCounter = 0;
		int sentenceCounter = 0;		
		
		while(nextLine != null){
			//nextLine = nextLine + " </s>";
			for (int i = 1; i < this.n; i++){
				nextLine = "<s> " + nextLine;
			}
			double probability = 1;
			double probabilityAddOne = 1;
			double probabilityGoodTuring = 1;
			
			
			
			nextLine = nextLine.replaceAll("\\s+", " ");
			
			//System.out.println("nextLine is: " + nextLine);
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
					
					probabilityAddOne = probabilityAddOne * smoother.getAddOnePoss(sentence);
					
					probabilityGoodTuring = probabilityGoodTuring * smoother.getGoodTuringPoss(sentence);
					
					
				}
				
				sentenceCounter++;
				
				if(probability == 0.0){unSmoothedZeroCounter++;}
				if(probabilityAddOne == 0.0){addOneZeroCounter++;}
				if(probabilityGoodTuring == 0.0){goodTuringZeroCounter++;}
				
				
				
				//System.out.printf("The probability for sentence: '%s' is: %e \n", nextLine, probability);
				this.manager.writeToFile("The probability without smoothing for: '" + nextLine + "' is:" + probability);
				this.manager.writeToFile("The probability with Add One smoothing is: " + probabilityAddOne); 
				this.manager.writeToFile("The probability with Good Turing smoothing: " + probabilityGoodTuring + "\n");
				//addToMap(nextLine, probability);
				nextLine = this.manager.readNextLine();
			}else{
				//System.out.println("The sentence was too short for the ngram size");
				nextLine = this.manager.readNextLine();
			}
		
		}

		System.out.println("Percentage of zeros in unsmoothed:" + 100*(unSmoothedZeroCounter/sentenceCounter));
		System.out.println("Percentage of zeros in Add One Smoothing:" + 100*(addOneZeroCounter/sentenceCounter));
		System.out.println("Percentage of zeros in Good Turing Smoothing:" + 100*(goodTuringZeroCounter/sentenceCounter));
		
		try{
			this.manager.terminate();
		}catch(Exception e){
			System.out.println(e);
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

