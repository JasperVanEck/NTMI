
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class NGram{
	
	HashMap<String, Integer> = new HashMap<String, Integer>();
	FileManager manager;
	int nGramSize;
	
	
	public NGram(String inputFile, int n) {
		nGramSize = n;
		
		inputFile = inputFile.replaceFirst("\\.txt", "");
		String outputFile = intputFile + "_" + n + "grams.txt";
				
		Filemanager manager = new FileManager(inputFile, outputFile);
		
		
	}
	
	public void computeNGrams(){
		
		Pattern splitPoint = Pattern.compile(" ");
		String nextLine = manager.nextLine();
		
		String[] words;
		
		while(nextLine != null){
			words = nextLine.split();
			
			
		
		
		}
	}
}