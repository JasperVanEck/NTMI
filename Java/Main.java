/* Authors Floris de Bruin(5772583), Gijs van Horn(10070370) en Jasper van Eck(6228194)
 * N-Gram frequency maker.
 * 
 */

import java.util.*;

public class Main{

	public static void main(String[] args){
		
		Scanner sc = new Scanner(System.in);
		String fileName = "test_sentences.txt";
		int n = 2;
		int m = 10;

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
					n = sc.nextInt();
				};
			}
			if(args[i].equals("-m")){
				try{
					m = Integer.parseInt(args[i + 1]);
				}catch(Exception e){
					System.out.printf("Dat was geen integer ja. Kies een integer...\n");
					m = sc.nextInt();
				}
			}
		}

		System.out.printf("N: %d \nFilename: %s\n", n, fileName);
		NGram nGram = new NGram(fileName, n);
	}
}
