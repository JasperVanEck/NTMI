/* Authors Floris de Bruin(5772583), Gijs van Horn(10070370) en Jasper van Eck(6228194)
 * N-Gram frequency maker.
 * Arguments from the command line are parsed to determine on what to run the N-Gram frequency maker.
 * Options for the command line are:
 * 	-n	supply an integer value, which you want to use as N-Grams.
 *	-m	supply an integer value, which will be the m most frequent N-Grams.
 *	-f	supply the file name, of the file you want to make N-Grams of.
 *	-af supply the additional file name, of the file that contains sentences for which the probabilities should be calculated.
 */

import java.util.*;

public class Main{

	public static void main(String[] args){
		
		Scanner sc = new Scanner(System.in);
		String fileName = "austen.txt";
		String additionalFile = "probability_sentences.txt";
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
		/* Run the NGramMaker class here */
		//NGram nGram = new NGram(fileName, n, m);
		//nGram.writeTopFrequencies();
		//nGram.printSumFrequencies();
		//nGram.printTopFrequencies();
		/* Run the probability calculator here */
		ProbabilityCalculator probCalc = new ProbabilityCalculator(additionalFile, fileName, n);
		//probCalc.calculate();
		probCalc.calculateArbitrary();
		probCalc.printTopTwoProbabilities();
	}
}
