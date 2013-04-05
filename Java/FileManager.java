
import java.io.*;
import java.util.regex.*;
import java.util.*;

public class FileManager {
	BufferedReader readerIn;
	String fileIn;
	FileWriter writerOut;
	String fileOut;
	int sizeIn;
	
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
		return completeFileAsString;
	}
	
	public String readNextLine() {
		try {
			return readerIn.readLine();
		} catch (IOException e) {
			System.err.println("There was an error reading from file " + fileIn);
			return "";
		}
	}
	
	public void terminate() throws IOException {
		readerIn.close();
		writerOut.close();
	}
}