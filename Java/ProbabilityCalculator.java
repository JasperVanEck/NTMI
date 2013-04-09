
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class ProbabilityCalculator{

	private NGram[] nGrams;
	private FileManager manager;
	private int n;	


	public ProbabilityCalculator(String file, int n){
		this.n = n;
		this.nGrams[n] = new NGram(file, n, 10);
		this.nGrams[n - 1] = new NGram(file, n - 1, 10);
		
		this.manager = new FileManager(file);
		
		
	}
	
	public double calculate(){
	
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
				
				double freq1 = nGrams[this.n].getValue(sentence);
				double freq2 = nGrams[this.n - 1].getValue(shortSentence);
				
				System.out.printf("Given '%s' the chance for '%s' is: %.10f \n", shortSentence, sentence, freq1/freq2);
				
				
				
			}
			
			nextLine = this.manager.readNextLine();
			
			
		}
		
		return 0;
	}	
	

}
