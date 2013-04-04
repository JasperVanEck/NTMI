/* Authors Floris de Bruin(5772583), Gijs van Horn(10070370) en Jasper van Eck(6228194)
 * N-Gram frequency maker.
 * 
 */

import java.util.*;

public class Main{

	public static void main(String[] args){
		
		String fileName = "austen.txt";
		int n = 2;

		for(int i = 0; i < args.length; i = i + 2)
		{
			if(args[i].equals("-f")){
				fileName = args[i + 1];
			}
			if(args[i].equals("-n")){
				try{
                                	n = Integer.parseInt(args[i + 1]);
                        	}catch(Exception e){
                                       	System.out.printf("Dat was geen integer ja. Kies een integer...\n");
                                       	Scanner sc = new Scanner(System.in);
                                       	n = sc.nextInt();
                                };
			}
		}

		System.out.printf("N: %d \nFilename: %s", n, fileName);
//		NGram nGram = new NGram(n, fileName);
	}
}
