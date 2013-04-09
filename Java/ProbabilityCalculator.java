
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class ProbabilityCalculator{

	private NGram[] nGrams;
	private FileManager manager;
	private int n;	
	private String corpusFile;

	public ProbabilityCalculator(String file, int n){
		this.n = n;
		this.nGrams = new NGram[2];
		this.nGrams[0] = new NGram(file, n);
		this.nGrams[1] = new NGram(file, n - 1);
		
		this.manager = new FileManager(file);
		
		
	}
	
	public ProbabilityCalculator(String add_file, String corpusFile){
		this.manager = new FileManager(add_file);
		this.corpusFile = corpusFile;
	
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
	
			String[] words = splitPoint.split(nextLine);
			
			
			while(){
				String[] wordsMinOne = Arrays.copyOfRange(words, 0, words.length-1);
			
			
			}
		
		}
	}
	
	public int getNGramArraySize(){
		return this.nGrams.size();
	}
	
	public void fillNGramArray(int n){
	
		int startpoint = getNGramArraySize() - 1;
		
		for(int i = startpoint; i < n; i++){
			NGram gram = new NGram(this.corpusFile, n);
			this.nGrams[i] = gram;
		}
		
	}
}

