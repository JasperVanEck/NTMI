/* Authors: Floris de Bruin(5772583), Gijs van Horn(10070370) en Jasper van Eck(6228194).
 * This clas generates permutations of a given String array. 
 * 
 */
 import java.util.*;
 import java.io.*;
 
public class Permutations {
	private static int counter = 1;
	private ArrayList<String[]> results;

	public static void main(String[] args){
		String[] array1 = {"1", "2", "3"};
		String[] array2 = {"1", "2", "3", "4"};
		String[] array3 = {"a", "b", "c"};
		String[] array4 = {"She", "daughters", "youngest", "was", "the", "of", "the", "two"};
		String[] array5 = {"She", "was", "the", "youngest"};
		Permutations perm = new Permutations(array4, "Array-Length8");
		//System.out.println(perm.generatePermutations(perm.getArray2()));
	}
	
	public Permutations(String[] arrayToPermute) {
		results = new ArrayList<String[]>();
		generatePermutations(arrayToPermute);
		writeResultsToFile("resultsOfPermutation");
	}
	
	public Permutations(String[] arrayToPermute, String fileTo) {
		results = new ArrayList<String[]>();
		generatePermutations(arrayToPermute);
		writeResultsToFile(fileTo);
	}
	
	public void generatePermutations(String[] permArray) {
		//System.out.println(counter+ ". " + Arrays.toString(permArray));
		//System.out.println(factorial(permArray.length));
		int fact = factorial(permArray.length);
		String resultString = "";
 		while(counter < fact) {
			//System.out.println("\t Step 1:");
			rightToLeft(permArray, permArray.length - 1, permArray.length - 1);
			//System.out.println("\t Step 2:");
			rightToLeft(permArray, permArray.length - 1, 1);
			//System.out.println("\t Step 3:");
			leftToRight(permArray, 0, permArray.length - 1);
			//System.out.println("\t Step 4:");
			rightToLeft(permArray, 1, 1);
		}
		//printResultsOfPermutation();
	}
	
	public void rightToLeft(String[] array, int element, int times) {
		int n = element;
		String resultString = "";
		
		for(int i = times; i > 0; i--) {
			String[] result = swap(array, n, n - 1);
			//resultString = resultString + " " + Arrays.toString(result) + "\n";
			//System.out.println(counter + ". " + Arrays.toString(result));
			String[] copy = new String[result.length];
			System.arraycopy(result,0,copy,0,result.length);
			results.add(copy);
			n--;
			this.counter++;
		}
	}
	
	public void leftToRight(String[] array, int element, int times) {
		int n = element;
		String resultString = "";
		
		for(int i = 0; i < times; i++) {
			String[] result = swap(array, n, n + 1);
			//resultString = resultString + " " + Arrays.toString(result) + "\n";
			//System.out.println(counter + ". " + Arrays.toString(result));
			String[] copy = new String[result.length];
			System.arraycopy(result,0,copy,0,result.length);
			results.add(copy);
			n++;
			this.counter++;
		}
	}
	
	public String[] swap(String[] array, int n, int m) {
		String first = array[n];
		array[n] = array[m];
		array[m] = first;
		return array;
	}
	
	public int factorial(int n) {
		int result = 1;
		for(int i = 1; i <= n; i++) {
			result *= i;
		}
		return result;
	}

	public void printResultsOfPermutation() {
		for(String[] elem : results) {
			System.out.println(Arrays.toString(elem));
		}
	}
	
	public ArrayList<String[]> getResultOfPermutation() {
		return this.results;
	}
	
	private void writeResultsToFile(String fileName) {
		String fileNameTxt = fileName + ".txt";
		try {
			FileWriter fstream = new FileWriter(fileNameTxt);
			BufferedWriter out = new BufferedWriter(fstream);
			
			for(String[] elem : this.results) {
				String elemToString = elem[0];
				for (int i = 1; i < elem.length; i++){
					elemToString += " " + elem[i];
				}
				out.write(elemToString + "\n");
			}
			
			out.close();
		} catch (Exception e) {
			System.out.println("Flabber");
		}
	}
}
