/* Authors Floris de Bruin(5772583), Gijs van Horn(10070370) en Jasper van Eck(6228194)
*
* Pos-Tagger
 */

import java.util.*;

public class Main{

	public static void main(String[] args){
		ProbabilityCalculator test = new ProbabilityCalculator("WSJ23.pos", "WSJ02-21.pos", 3, 4);
		test.createPosTagSequences();
	}
}
