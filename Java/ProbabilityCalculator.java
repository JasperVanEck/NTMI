
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class ProbabilityCalculator{

	private NGram nGram;
	private NGram nMinOneGram;
	private FileManager manager;
	
	public ProbabilityCalculator(NGram nGram, NGram nMinOneGram, String file){
		this.nGram = nGram;
		this.nMinOneGram = nMinOneGram;
		
		this.manager = new FileManager(file);
		
		
	}
	
	public double calculate(){
	
		Pattern splitPoint = Pattern.compile(" ");
		String nextLine = this.manager.readNextLine();
		
		int n = nGram.getN();
				
		while(nextLine != null){
		
			String[] words = splitPoint.split(nextLine);
			
			int wordsLength = words.length;
			
			if(wordsLength == n){
				String[] wordsMinOne = new String[words.length-1];
				wordsMinOne = Arrays.copyOfRange(words, 0, words.length-1);
				
				String sentence = ("" + Arrays.asList(words)).replaceAll("(^.|.$)", "").replace(", ", " ");
				String shortSentence = ("" + Arrays.asList(wordsMinOne)).replaceAll("(^.|.$)", "").replace(", ", " ");;
				
				double freq1 = nGram.getValue(sentence);
				double freq2 = nMinOneGram.getValue(shortSentence);
				
				System.out.printf("Given '%s' the chance for '%s' is: %.10f \n", shortSentence, sentence, freq1/freq2);
				
				
				
			}
			
			nextLine = this.manager.readNextLine();
			
			
		}
		
		return 0;
	}	
	

}