/* Authors: Floris de Bruin(5772583), Gijs van Horn(10070370) en Jasper van Eck(6228194.)
 * The FileManager class allows us to read from a .txt file, so that we may use the text hidden within.
 * It's also possible to write to a .txt file, so that we may later use the NGrams created.
 */

import java.io.*;
import java.util.regex.*;
import java.util.*;

public class FileManager {
	private BufferedReader readerIn;
	private String fileIn;
	private FileWriter writerOut;
	private String fileOut;
	private int sizeIn;
	private String nextReadSentence;
	
	public static void main(String[] args){
		FileManager test = new FileManager("WSJ02-21.pos");
		test.readNextSentence();
		test.readNextSentence();
		test.readNextSentence();
		
	}
	
	//Default constructor, only creates a file reader.
	public FileManager(String fileIn) {
		this.fileIn = fileIn;
		//System.out.println(this.fileIn);
		
		//this.fileIn = fileIn;
		try {
			readerIn = new BufferedReader(new FileReader(fileIn),2);
		} catch (Throwable e) {
			System.err.println("There was an error opening the file...");
			System.exit(1);
		}	
	}

	//Opens a file reader, and a file writer.
	public FileManager(String fileIn, String fileOut) {
		this.fileIn = fileIn;
		//System.out.println(this.fileIn);
		
		//this.fileIn = fileIn;
		this.fileOut = fileOut;
		try {
			readerIn = new BufferedReader(new FileReader(this.fileIn),2);
			writerOut = new FileWriter(this.fileOut);
		} catch (Throwable e) {
			System.err.println("There was an error opening the file...");
			System.exit(1);
		}
	}
	
	public void writeToFile(String data) {
		try {
			//System.out.println("Writing: " + data);
			this.writerOut.write(data + "\n");
		} catch (IOException e) {
			System.out.println("There was a problem writing to the file");
		}
	}

	//Reads the whole file, by appending every read line. Also replaces newlines with a single space.
	public String readWholeFile() {
		String completeFileAsString = "";
		String currentLine = "";
		while(currentLine != null) {
			try {
				currentLine = readerIn.readLine();
				completeFileAsString += currentLine + " ";
			} catch (IOException e) {
				System.err.println("There was an error reading from file " + fileIn);
				return "";
			}
		}
		completeFileAsString = completeFileAsString.substring(0, completeFileAsString.length() - 1);
		completeFileAsString = completeFileAsString.replace("   "," ").replace("  "," ");
		return completeFileAsString;
	}

	//Reads the next line from the file.
	public String readNextLine() {
		try {
			return readerIn.readLine();
		} catch (IOException e) {
			System.err.println("There was an error reading from file " + fileIn);
			return "";
		}
	}
	
	// Reads next line of file, 
	public String readNextLineWithDummySymbols() {
		String currentLine = "";
		try {
			// first sentence of text needs a <s> symbol.
			if (nextReadSentence == null)
			{
				currentLine = "<s> " + readerIn.readLine();
			} else {
				currentLine = nextReadSentence;
			}
			
			nextReadSentence = readerIn.readLine();
			
			if (nextReadSentence == null) {
				return null;
			}
			
			if (currentLine.isEmpty() && !nextReadSentence.isEmpty()) {
				nextReadSentence = "<s> " + nextReadSentence;
			}
			
			if ( nextReadSentence.isEmpty() && !currentLine.isEmpty()) {
				currentLine = currentLine + " </s>";
			} 
			//System.out.println(currentLine);
			return currentLine;
			
		} catch (IOException e) {
			System.err.println("There was an error reading from file " + fileIn);
			return "";
		}
	}
	
	public ArrayList<String[]> readNextSentence() {
		ArrayList<String[]> sentence = new ArrayList<String[]>();
		String lastWord = "";

		while (!lastWord.equals("./.") && !lastWord.equals("\\=+")) {
			try {
				String currentLine = readerIn.readLine();
				if (currentLine.isEmpty()){
 					//System.out.println("Empty Sentence");
					continue;
				}
				
				if(currentLine == null){
					System.out.println("Returning null");
					return null;
				}
				
				String[] currentLineArray = currentLine.split(" ");
				//System.out.println(currentline);
				
				for (String elem : currentLineArray)
				{
					if (elem.equals("./.") || elem.equals("\\=+")) {
// 						System.out.println("==================== + " + elem);
						lastWord = elem;
					} else {
						String[] splitWordPos = elem.split("/");
						if(splitWordPos.length == 2){
// 							System.out.println(splitWordPos[0] + "/" + splitWordPos[1]);
// 							System.out.println(splitWordPos.length);
							if (splitWordPos[1].matches("[A-Za-z0-9]*")){
// 								System.out.println("Added to Sentence");
								sentence.add(splitWordPos);
							}
						}
					}
				}
			} catch (Exception e) {
				return null;
				//System.out.println("Something went wrong" + e);
			}
		} //endWhile
		//Object[] completeSentence = sentence.toArray();
// 		System.out.println(Arrays.deepToString(completeSentence));
		return sentence;
	}

	//Closes the file reader and writer.	
	public void terminate() throws IOException {
		readerIn.close();
		writerOut.close();
	}
}
