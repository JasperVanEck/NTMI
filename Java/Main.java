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

		ProbabilityCalculator probCalc = new ProbabilityCalculator("WSJ23.pos", "WSJ02-21.pos", 3, 4);
		//probCalc.calculateSmoothedPos();
		probCalc.creatPosTagSequences();
	}
}
