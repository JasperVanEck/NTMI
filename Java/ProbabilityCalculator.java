
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class ProbabilityCalculator{

	private FileManager manager;
	private int n,k;
	private String trainFile, testFile;
	
	/**
	* Constructor for probability Calculator, sets all variables that are used in the create pos tag sequences
	* for the add test corpus.
	**/
	public ProbabilityCalculator(String testCorpus, String corpusFile, int n, int k){
		this.n = n;
		this.k = k;
		this.trainFile = corpusFile;
		this.testFile = testCorpus;
		this.manager = new FileManager(testCorpus);
	}

	/**
	* createPosTagSequences() calculates the most likely tag for all the words in the test sentences,
	* the max-length of the test Sentences is set as 15 in this function as stated in the assingment, but the program
	* works just as well with no max length(maxLineLength > 100000).
	**/
	public void createPosTagSequences(){
		int maxLineLength = 15;
		NGram trigrams = new NGram(trainFile, 3);
		NGram bigrams = new NGram(trainFile, 2);
		NGram dictionaryCreator = new NGram(trainFile);
		Smoothing smoother = new Smoothing();
		HashMap<ArrayList<String[]>, ArrayList<String[]>> results = new HashMap<ArrayList<String[]>, ArrayList<String[]>>();
		HashMap<String, Integer> trigramsOfPosTags= trigrams.computeNGramsPosTag();
		HashMap<String, Integer> bigramsOfPosTags= bigrams.computeNGramsPosTag();
		int totalSentencesInTrainingCorpus = trigrams.getTotalSentences();
		HashMap<String, Double> postagTrigramPossibilities = smoother.goodTuringPos(trigramsOfPosTags, bigramsOfPosTags, this.k, totalSentencesInTrainingCorpus);
		Map<String, Map<String, Integer>> postagDictionary = dictionaryCreator.createPosTagDictionaryWithWordsAndCount();
		HashMap<String, HashMap<String, Double>> posTagDictionaryWithWordsAndPoss = smoother.goodTuringPosTagsCalcPossibilities(postagDictionary);
		
		//Retrieve the first sentence from the testSentence file. Check if sentence is not bigger than the max length allowed.
		ArrayList<String[]> nextLine = this.manager.readNextSentence();
		while(nextLine.size() > maxLineLength){
			nextLine = this.manager.readNextSentence();
		}
		
		//Create a file to write the results to
		try{
			FileWriter fstream = new FileWriter("resultsOfTagger.txt");
			BufferedWriter out = new BufferedWriter(fstream);
		
			//The important loop to loop over all the sentences in test file and do the calculations.
			while(nextLine != null){
				//The line read can be 0 in length if that is the case retrieve a new allowed sentence and continue
				//with a new loop.
				if(nextLine.size() == 0)
				{
					nextLine = this.manager.readNextSentence();
					while(nextLine.size() > maxLineLength){
						nextLine = this.manager.readNextSentence();
					
					}
					continue;
				}
				//Add the start symbols to the sentence that is tested.
				String sentence = "<s> <s> ";
				for(String[] elem : nextLine){
					sentence += elem[0] + " ";
				}
				sentence = sentence.substring(0, sentence.length()-1);
				
				//Create all the trigrams of the sentence we are testing.
				String[] sentenceTrigrams = dictionaryCreator.createNGramsOfSentence(sentence.split(" "), 3);
				
				//Function that tags the sentence and returns the results in an ArrayList with the words POS-tag pair.
				ArrayList<String[]> sentenceTagged = createPosTagSequenceForSentence(sentenceTrigrams, postagTrigramPossibilities, posTagDictionaryWithWordsAndPoss);
				
				//Put the result in a map for later usage where we evaluate the Recall and Precision of the result.
				results.put(nextLine, sentenceTagged);
				
				//Write the results in a file, 0- is the original testSentence, T- is the sentence with the Tags
				//that are added by the tagger.
				out.write("O - " + Arrays.deepToString(nextLine.toArray())+ "\n" + "T - " + Arrays.deepToString(sentenceTagged.toArray()) + "\n\n");
				
				//Retrieve a new testSentence
				nextLine = this.manager.readNextSentence();
				
				while(nextLine != null && nextLine.size() > maxLineLength){
					nextLine = this.manager.readNextSentence();
				}
			}
			//close the file we are writing to
			out.close();
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		//Calculate the Precision and Recall of the results of the Tagger.
		correctnessOfTaggerForSentence(results);
	}
	
	/**
	* createPosTagSequenceForSentence(sentenceTrigrams,postagTrigramsPoss, posTagDictionaryWithWordsAndPoss)
	* recieves a String array of trigrams of the sentence that we are tagging. This function return a Arraylist
	* String arrays that contain the word and POS-tag given by the function.
	**/
	public ArrayList<String[]> createPosTagSequenceForSentence(String[] sentenceTrigrams, 
																HashMap<String, Double> postagTrigramsPoss,
																HashMap<String, HashMap<String, Double>> posTagDictionaryWithWordsAndPoss){
		
		double viterbiPoss = 1;
		double bestValue = 0;
		String bestTag = "";
		String bigram = "";
		String currentTag = "";
		
		ArrayList<String[]> taggedSentence = new ArrayList<String[]>();
		//Split the trigram on the space and create a bigram of the first two elements.
		String[] startSentence = sentenceTrigrams[0].split(" ");
		String[] predecessorBigram = {startSentence[0], startSentence[1]};
		
		//Loop over all the trigrams in the sentence trigrams.
		for(String trigram : sentenceTrigrams){
			HashMap<String, Double> postagsWithPossGivenPredecessors = new HashMap<String, Double>();
			HashMap<String, Double> wordGivenPostagPossibilities = new HashMap<String, Double>();
			HashMap<String, Double> intermediateViterbiPossMap = new HashMap<String, Double>();
			
			String[] trigramArray = trigram.split(" ");
			String wordToTag = trigramArray[2];
			/**
			Deze for loop haalt alle mogelijke postags op gegeven de twee voorgangers, en de kans daarop. En stopt deze in de hashmap postagsWithPossGivenPredecessors.
			**/
			
			for(Map.Entry<String, Double> elem : postagTrigramsPoss.entrySet()){
				String[] currentElement = elem.getKey().split(" ");
				if(predecessorBigram[0].equals(currentElement[0]) && predecessorBigram[1].equals(currentElement[1])){
					postagsWithPossGivenPredecessors.put(currentElement[2], elem.getValue());
				}
				
			}		
			
			/**
			Deze for-loop haalt voor elke tag uit de vorige loop de mogelijke woorden op met hun kans gegeven die tag en zet deze in wordGivenPostagPossibilities. 
			Als het niet bekent is haalt hij de kans op dat het woord niet voorkomt bij deze tag.
			**/
			
			for(Map.Entry<String, Double> postagEntry : postagsWithPossGivenPredecessors.entrySet()){
				currentTag = postagEntry.getKey();
				Map<String, Double> wordsAndPossibilityForTag = posTagDictionaryWithWordsAndPoss.get(currentTag);
				if(wordsAndPossibilityForTag.containsKey(wordToTag)){
					wordGivenPostagPossibilities.put(currentTag, wordsAndPossibilityForTag.get(wordToTag));					
				}else{
					wordGivenPostagPossibilities.put(currentTag, wordsAndPossibilityForTag.get("0Count"));					
				}

			}
			
			/**
			Deze loop haalt de kansen uit de vorige loops en vermenigvuldigt deze met elkaar.
			**/
			
			for(Map.Entry<String, Double> postagWithPossGivenPre : postagsWithPossGivenPredecessors.entrySet()){
				String tag = postagWithPossGivenPre.getKey();
				double wordPoss = wordGivenPostagPossibilities.get(tag);
				double intermediateViterbiPoss = postagWithPossGivenPre.getValue() * wordPoss;
				intermediateViterbiPossMap.put(tag, intermediateViterbiPoss);
			}
						
			/**
			Deze loop selecteert de hoogste waarde uit het product van de vorige loop.
			**/
			for(Map.Entry<String, Double> intermediatePoss : intermediateViterbiPossMap.entrySet()){
				if(bestValue < intermediatePoss.getValue()){
					bestValue = intermediatePoss.getValue();
					bestTag = intermediatePoss.getKey();
				}
			}
			//Create a new bi-gram of the new tag given to the word and the last POS tag of the previous bigram.
			predecessorBigram[0] = predecessorBigram[1];
			predecessorBigram[1] = bestTag;
			//Add the word with its new tag to the results arrayList.
			String[] temp = {wordToTag, bestTag};
			taggedSentence.add(temp);
			//Calculate the new Viterbi Possibility
			viterbiPoss *= bestValue;
			
			bestValue = 0;
			bestTag = "";
		
		}
		return taggedSentence;
		
	}
	
	public void correctnessOfTaggerForSentence(HashMap<ArrayList<String[]>, ArrayList<String[]>> results){
		HashMap<String, Integer> truePositives = new HashMap<String, Integer>();
		HashMap<String, Integer> falsePositives = new HashMap<String, Integer>();
		HashMap<String, Integer> trueNegatives = new HashMap<String, Integer>();
		HashMap<String, Integer> falseNegatives = new HashMap<String, Integer>();
		
		for(Map.Entry<ArrayList<String[]>,ArrayList<String[]>> entry : results.entrySet()){
			ArrayList<String[]> originalSentence = entry.getKey();
			ArrayList<String[]> taggedByTaggerSentence = entry.getValue();
			int index = 0;
			for(String[] wordAndTag : originalSentence) {
				//als correct getagged
				if (wordAndTag[1].equals(taggedByTaggerSentence.get(index)[1])){
					if(truePositives.containsKey(wordAndTag[1])){
						int truePos = truePositives.get(wordAndTag[1]);
						truePositives.put(wordAndTag[1], truePos+1);
					} else {
						truePositives.put(wordAndTag[1], 1);
					}
				} else {
					if(falseNegatives.containsKey(wordAndTag[1])){
						int falseNeg = falseNegatives.get(wordAndTag[1]);
						falseNegatives.put(wordAndTag[1], falseNeg+1);
					} else {
						falseNegatives.put(wordAndTag[1], 1);
					}
				}
				index++;
			}
			index = 0;
			for(String[] wordAndTagByTagger : taggedByTaggerSentence){
				if(!wordAndTagByTagger[1].equals(originalSentence.get(index)[1])){
					if(falsePositives.containsKey(wordAndTagByTagger[1])){
						int falsePos = falsePositives.get(wordAndTagByTagger[1]);
						falsePositives.put(originalSentence.get(index)[1], falsePos+1);
					} else {
						falsePositives.put(originalSentence.get(index)[1], 1);
					}
				}
				index++;
			}
		}
		
		for(String pos : truePositives.keySet()){
			int trueNegativeValue = totalCountFromHashMapExclude(truePositives, pos);
			trueNegatives.put(pos, trueNegativeValue);
		}
		
		//System.out.println("True Positives: \n"+ truePositives + "\n\n");
		//System.out.println("False Positives: \n" + falsePositives + "\n\n");
		//System.out.println("True Negatives: \n" + trueNegatives + "\n\n");
		//System.out.println("False Negatives: \n" + falseNegatives + "\n\n");
		
		precision(truePositives, falsePositives);
		recall(truePositives, falseNegatives);
	}
	
	public void precision(HashMap<String, Integer> truePositives, HashMap<String, Integer> falsePositives){
		try{
			FileWriter fstream = new FileWriter("precision.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("======Precision Values per Tag======\n");
		System.out.println("======Precision Values per Tag======\n");
		
		for(Map.Entry<String, Integer> entry : truePositives.entrySet()){
			String tag = entry.getKey();
			int falsePositivesValue = 0;
			if(falsePositives.containsKey(tag)){
				falsePositivesValue = falsePositives.get(tag);
			}
			double precision = (double) entry.getValue() / (double)(entry.getValue() + falsePositivesValue);
			System.out.println(tag + " --- " + precision*100 + "%");
			out.write(tag + " --- " + precision*100 + "%\n");
		}
		System.out.println("\n\n");
		out.close();
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void recall(HashMap<String, Integer> truePositives, HashMap<String, Integer> falseNegatives){
		  try{
			FileWriter fstream = new FileWriter("recall.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("======Recall Values per Tag======\n");
		System.out.println("======Recall Values per Tag======\n");
		for(Map.Entry<String, Integer> entry : truePositives.entrySet()){
			String tag = entry.getKey();
			int falseNegativesValue = 0;
			if(falseNegatives.containsKey(tag)){
				falseNegativesValue = falseNegatives.get(tag);
			}
			double recall = (double) entry.getValue() / (double)(entry.getValue() + falseNegativesValue);
			System.out.println(tag + " --- " + recall*100 + "%");
			out.write(tag + " --- " + recall*100 + "%\n");
		}
		out.close();
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public int totalCountFromHashMapExclude(HashMap<String, Integer> map, String key){
		int total = 0;
		
		for(String entryKey : map.keySet()){
			if(!entryKey.equals(key)){
				total += map.get(key);
			}
		}
		return total;
	}	
}

