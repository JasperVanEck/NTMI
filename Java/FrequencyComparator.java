/* Floris de Bruin(5772583), Gijs van Horn(10070370) en Jasper van Eck(6228194)
 * FrequencyComparator allows us to check if a certain entry in the HashMap is larger, 
 * than the other.
 */

import java.util.*;

class FrequencyComparator implements Comparator<String> {

	Map<String, Integer> base;
	//constructor
	public FrequencyComparator(HashMap<String, Integer> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.    
	@Override 
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}
