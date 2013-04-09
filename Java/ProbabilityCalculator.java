
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class ProbabilityCalculator{

	private NGram[] nGrams;
	private FileManager manager;
	private int n;	

	public ProbabilityCalculator(String add_file, String corpusFile, int n){
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
	
		Pattern splitPoint = pattern.compile(" ");
		String nextLine = this.manager.readNextLine();	
		
		while(nextLine != null){
		
			for (int i = 0; i < this.n; i++){
				nextLine = "<s> " + nextLine;
			}
			
			String[] words = splitPoint.split(nextLine);
			fillNGramArray(words.length);
			
			while(){
				
				
			
			}
		
		}
	}
	
}

