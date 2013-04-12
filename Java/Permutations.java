/* Authors: Floris de Bruin(5772583), Gijs van Horn(10070370) en Jasper van Eck(6228194.)
 * 
 * 
 */
 import java.util.*;
 import java.util.Arrays;
 import java.util.Collections;
 import java.lang.reflect.Array;
 
public class Permutations {
	private static int counter = 1;
	public static void main(String[] args){
		String[] array1 = {"1", "2", "3"};
		String[] array2 = {"1", "2", "3", "4"};
		String[] array3 = {"a", "b", "c"};
		String[] array4 = {"She", "daughters", "youngest", "was", "the", "of", "the", "two"};
		String[] array5 = {"She", "was", "the", "youngest"};
		Permutations perm = new Permutations(array5);
	}
	
	public Permutations(String[] permArray) {
		generatePermutations(permArray);
	}
	
	public void generatePermutations(String[] permArray) {
		//System.out.println(counter+ ". " + Arrays.toString(permArray));
		//System.out.println(factorial(permArray.length));
		int fact = factorial(permArray.length);
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
	}
	
	public void rightToLeft(String[] array, int element, int times) {
		int n = element;
		
		for(int i = times; i > 0; i--) {
			String[] result = swap(array, n, n - 1);
			System.out.println(counter + ". " + Arrays.toString(result));
			n--;
			counter++;
		}
	}
	
	public void leftToRight(String[] array, int element, int times) {
		int n = element;
		
		for(int i = 0; i < times; i++) {
			String[] result = swap(array, n, n + 1);
			System.out.println(counter + ". " + Arrays.toString(result));
			n++;
			counter++;
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
}
