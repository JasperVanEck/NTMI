/* Authors Floris de Bruin(5772583), Gijs van Horn(10070370) en Jasper van Eck(6228194)
 * N-Gram frequency maker.
 * Arguments from the command line are parsed to determine on what to run the N-Gram frequency maker.
 * Options for the command line are:
 * 	-n	supply an integer value, which you want to use as N-Grams.
 *	-m	supply an integer value, which will be the m most frequent N-Grams.
 *	-f	supply the file name, of the file you want to make N-Grams of.
 */

import java.util.*;

public class Main{

	public static void main(String[] args){
		
		Scanner sc = new Scanner(System.in);
		String fileName = "test_sentences.txt";
		String additionalFile = "add_sentences.txt";
		int n = 2;
		int m = 10;

		for(int i = 0; i < args.length; i = i + 2)
		{
			if(args[i].equals("-f")){
				fileName = args[i + 1];
			}
			if(args[i].equals("-af")){
				additionalFile = args[i + 1];
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

		System.out.printf("N: %d \nFilename: %s\nAdditional file: %s\n", n, fileName, additionalFile);
		//NGram nGram = new NGram(fileName, n, m);
		//nGram.writeTopFrequencies();
		//nGram.printSumFrequencies();
		//nGram.printTopFrequencies();
		//NGram nMinOneGram = new NGram(fileName, n - 1, m);
		//nMinOneGram.printSumFrequencies();
		//nMinOneGram.printTopFrequencies();
<<<<<<< HEAD
		ProbabilityCalculator probCalc = new ProbabilityCalculator(additionalFile, fileName,  n);
		probCalc.calculate();
=======
		ProbabilityCalculator probCalc = new ProbabilityCalculator(additionalFile, fileName, n);
		//probCalc.calculate();
		probCalc.calculateArbitrary();
>>>>>>> 0a63f306a7e4c247d48e13b1945a6b5a4c1f7fc7
	}
}
