
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class NGram{
	
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	FileManager manager;
	int nGramSize;
	
	public NGram(String inputFile, int n) {
		nGramSize = n;
		
		String inputFileName = inputFile.replaceFirst("\\.txt", "");
		String outputFile = inputFileName + "_" + n + "grams.txt";
				
		FileManager manager = new FileManager(inputFile, outputFile);
		
		
	}
	
	public void computeNGrams(){
		
		Pattern splitPoint = Pattern.compile(" ");
		
		String nextLine = manager.readNextLine();
		
		String nGram = "";
		
		String[] words;
		
		while(nextLine != null){
			words = splitPoint.split(nextLine);
			int lineSize = words.length - nGramSize - 1;
			
			for(int i=0; i<lineSize; i++){
				nGram = words[i];
				System.out.println(nGram);
				for(int j=1; j < nGramSize; i++){
					nGram = nGram + " " + words[i+j];
				}
				addToMap(nGram);
				
			}
		
		
		}
	}
	
	public void addToMap(String nGram){
	
	}
}