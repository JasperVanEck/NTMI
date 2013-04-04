
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class NGram{
	
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	FileManager manager;
	int nGramSize;
	
	
	public NGram(String inputFile, int n) {
		nGramSize = n;
		
		inputFile = inputFile.replaceFirst("\\.txt", "");
		String outputFile = inputFile + "_" + n + "grams.txt";
				
		FileManager manager = new FileManager(inputFile, outputFile);
		
		
	}
	
	public void computeNGrams(){
		
		Pattern splitPoint = Pattern.compile(" ");
		String nextLine = manager.readNextLine();
		
		String[] words;
		
		while(nextLine != null){
			words = splitPoint.split(nextLine);
			
			
		
		
		}
	}
}