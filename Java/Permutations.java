/* Authors: Floris de Bruin(5772583), Gijs van Horn(10070370) en Jasper van Eck(6228194).
 * This clas generates permutations of a given String array. 
 * 
 */
 import java.util.*;
 import java.util.Arrays;
 import java.util.Collections;
 import java.lang.reflect.Array;
 
public class Permutations {
	private static int counter = 1;
	private String[] array1 = {"She", "daughters", "youngest", "was", "the", "of", "the", "two"};
	private String[] array2 = {"She", "was", "the", "youngest"};
	public static void main(String[] args){
		String[] array1 = {"1", "2", "3"};
		String[] array2 = {"1", "2", "3", "4"};
		String[] array3 = {"a", "b", "c"};
		String[] array4 = {"She", "daughters", "youngest", "was", "the", "of", "the", "two"};
		String[] array5 = {"She", "was", "the", "youngest"};
		Permutations perm = new Permutations();
		System.out.println(perm.generatePermutations(perm.getArray2()));
	}
	
	public Permutations() {
		//generatePermutations(permArray);
	}
	
	public String generatePermutations(String[] permArray) {
		//System.out.println(counter+ ". " + Arrays.toString(permArray));
		//System.out.println(factorial(permArray.length));
		int fact = factorial(permArray.length);
		String resultString = "";
 		while(counter < fact) {
			//System.out.println("\t Step 1:");
			resultString = resultString + rightToLeft(permArray, permArray.length - 1, permArray.length - 1) + "\n";
			//System.out.println("\t Step 2:");
			resultString = resultString + rightToLeft(permArray, permArray.length - 1, 1) + "\n";
			//System.out.println("\t Step 3:");
			resultString = resultString + leftToRight(permArray, 0, permArray.length - 1) + "\n";
			//System.out.println("\t Step 4:");
			resultString = resultString + rightToLeft(permArray, 1, 1) + "\n";
		}
		return resultString;
	}
	
	public String rightToLeft(String[] array, int element, int times) {
		int n = element;
		String resultString = "";
		
		for(int i = times; i > 0; i--) {
			String[] result = swap(array, n, n - 1);
			resultString = resultString + " " + Arrays.toString(result) + "\n";
			//System.out.println(counter + ". " + Arrays.toString(result));
			n--;
			this.counter++;
		}
		return resultString;
	}
	
	public String leftToRight(String[] array, int element, int times) {
		int n = element;
		String resultString = "";
		
		for(int i = 0; i < times; i++) {
			String[] result = swap(array, n, n + 1);
			resultString = resultString + " " + Arrays.toString(result) + "\n";
			//System.out.println(counter + ". " + Arrays.toString(result));
			n++;
			this.counter++;
		}
		return resultString;
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
	
	public String[] getArray1(){
		return this.array1;
	}

	public String[] getArray2(){
		return this.array2;
	}
}
