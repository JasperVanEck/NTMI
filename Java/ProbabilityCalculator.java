
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class ProbabilityCalculator{

	private NGram[] nGrams;
	private FileManager manager;
	private Smoothing smoother;
	private int n,k;
	private TreeMap<Double, String> sortedSentences = new TreeMap<Double, String>();
	
	public static void main(String[] args){
		ProbabilityCalculator test = new ProbabilityCalculator("WSJ23.pos", "WSJ02-21.pos", 3, 4);
		//test.calculateSmoothedPos();
		test.createPosTagSequences();
	}

	public ProbabilityCalculator(String addFile, String corpusFile, int n, int k){
		this.n = n;
		this.k = k;
		this.nGrams = new NGram[2];
		this.nGrams[0] = new NGram(corpusFile, n);
		this.nGrams[1] = new NGram(corpusFile, n - 1);
		
		this.manager = new FileManager(addFile);
	}
	
	public ProbabilityCalculator(String testFile){
		this.n = 3;
		this.smoother = new Smoothing("WSJ02-21.pos");
		this.manager = new FileManager(testFile, "evaluated_sentences.txt");
	}
	
	public void calculate(){
	
		Pattern splitPoint = Pattern.compile(" ");
		String nextLine = this.manager.readNextLine();
				
		while(nextLine != null){
		
			String[] words = splitPoint.split(nextLine);
			
			int wordsLength = words.length;
			
			if(wordsLength == this.n){
				String[] wordsMinOne = new String[words.length-1];
				wordsMinOne = Arrays.copyOfRange(words, 0, words.length-1);
				
				String sentence = ("" + Arrays.asList(words)).replaceAll("(^.|.$)", "").replace(", ", " ");
				String shortSentence = ("" + Arrays.asList(wordsMinOne)).replaceAll("(^.|.$)", "").replace(", ", " ");;
				
				double freq1 = nGrams[0].getValue(sentence);
				double freq2 = nGrams[1].getValue(shortSentence);
				
				System.out.printf("Given '%s' the chance for '%s' is: %.10f \n", shortSentence, sentence, freq1/freq2);
				
			}
			
			nextLine = this.manager.readNextLine();			
		}
	}
	
	public void calculateArbitrary(){
	
		Pattern splitPoint = Pattern.compile(" ");
		String nextLine = this.manager.readNextLine();			
		
		while(nextLine != null){
			//nextLine = nextLine + " </s>";
			for (int i = 1; i < this.n; i++){
				nextLine = "<s> " + nextLine;
			}
			double probability = 1;
			//System.out.println(nextLine);
			String[] words = splitPoint.split(nextLine);
			if(words.length - this.n > this.n){
				
				boolean firstrun = true;
							
				for(int i=this.n; i < words.length; i++ ){
					String[] tempGramMinOne = Arrays.copyOfRange(words, i-this.n + 1, i);
					String[] tempGram = Arrays.copyOfRange(words, i-this.n + 1, i+1);
					
					String shortSentence = ("" + Arrays.asList(tempGramMinOne)).replaceAll("(^.|.$)", "").replace(", ", " ");
					String sentence = ("" + Arrays.asList(tempGram)).replaceAll("(^.|.$)", "").replace(", ", " ");
					
					//System.out.println("shortsentence is: " + shortSentence);
					//System.out.println("sentence is: " + sentence);
					
					
					double freq2;
					double freq1;
					
					try{
						freq1 = nGrams[0].getValue(sentence);
						if(firstrun){
							 freq2 = nGrams[1].getTotalSentences();
							 firstrun = false;
						}else{
							freq2 = nGrams[1].getValue(shortSentence);
						}
					} catch(Exception e){
						freq1 = 0;
						freq2 = 1;
					}
					
					probability = probability * (freq1/freq2);
					//System.out.println(probability);
					
					
				}
				//System.out.printf("The probability for sentence: '%s' is: %e \n", nextLine, probability);
				addToMap(nextLine, probability);
				nextLine = this.manager.readNextLine();
			}else{
				//System.out.println("The sentence was too short for the ngram size");
				nextLine = this.manager.readNextLine();
			}
		
		}
	}
	
	/**
	This function performs probability calculations on sentences which are each one on their own line.
	It writes the unsmoothed, add-one smoothed and Good-Turing smoothed probabilities to the file evaluated_sentences.txt.
	**/
	public void calculateSmoothed(){
	
		Pattern splitPoint = Pattern.compile(" ");
		String nextLine = this.manager.readNextLine();	

		double unSmoothedZeroCounter = 0;
		double addOneZeroCounter = 0;
		double goodTuringZeroCounter = 0;
		int sentenceCounter = 0;		
		
		while(nextLine != null){
			//nextLine = nextLine + " </s>";
			for (int i = 1; i < this.n; i++){
				nextLine = "<s> " + nextLine;
			}
			double probability = 1;
			double probabilityAddOne = 1;
			double probabilityGoodTuring = 1;
			
			
			
			nextLine = nextLine.replaceAll("\\s+", " ");
			
			//System.out.println("nextLine is: " + nextLine);
			String[] words = splitPoint.split(nextLine);
			if(words.length - this.n > this.n){
				
				boolean firstrun = true;
							
				for(int i=this.n; i < words.length; i++ ){
					String[] tempGramMinOne = Arrays.copyOfRange(words, i-this.n + 1, i);
					String[] tempGram = Arrays.copyOfRange(words, i-this.n + 1, i+1);
					
					String shortSentence = ("" + Arrays.asList(tempGramMinOne)).replaceAll("(^.|.$)", "").replace(", ", " ");
					String sentence = ("" + Arrays.asList(tempGram)).replaceAll("(^.|.$)", "").replace(", ", " ");
					
					//System.out.println("shortsentence is: " + shortSentence);
					//System.out.println("sentence is: " + sentence);
					
					
					double freq2;
					double freq1;
					
					try{
						freq1 = nGrams[0].getValue(sentence);
						if(firstrun){
							 freq2 = nGrams[1].getTotalSentences();
							 firstrun = false;
						}else{
							freq2 = nGrams[1].getValue(shortSentence);
						}
					} catch(Exception e){
						freq1 = 0;
						freq2 = 1;
					}
					
					probability = probability * (freq1/freq2);
					//System.out.println(probability);
					
					probabilityAddOne = probabilityAddOne * smoother.getAddOnePoss(sentence);
					
					probabilityGoodTuring = probabilityGoodTuring * smoother.getGoodTuringPoss(sentence);
					
					
				}
				
				sentenceCounter++;
				
				if(probability == 0.0){unSmoothedZeroCounter++;}
				if(probabilityAddOne == 0.0){addOneZeroCounter++;}
				if(probabilityGoodTuring == 0.0){goodTuringZeroCounter++;}
				
				
				
				//System.out.printf("The probability for sentence: '%s' is: %e \n", nextLine, probability);
				this.manager.writeToFile("The probability without smoothing for: '" + nextLine + "' is:" + probability);
				this.manager.writeToFile("The probability with Add One smoothing is: " + probabilityAddOne); 
				this.manager.writeToFile("The probability with Good Turing smoothing: " + probabilityGoodTuring + "\n");
				//addToMap(nextLine, probability);
				nextLine = this.manager.readNextLine();
			}else{
				//System.out.println("The sentence was too short for the ngram size");
				nextLine = this.manager.readNextLine();
			}
		
		}
		System.out.println("Percentage of zeros in unsmoothed:" + 100*(unSmoothedZeroCounter/sentenceCounter));
		System.out.println("Percentage of zeros in Add One Smoothing:" + 100*(addOneZeroCounter/sentenceCounter));
		System.out.println("Percentage of zeros in Good Turing Smoothing:" + 100*(goodTuringZeroCounter/sentenceCounter));
		
		try{
			this.manager.terminate();
		}catch(Exception e){
			System.out.println(e);
		}
	}

	/**
	*
	**/
	public void createPosTagSequences(){
		int maxLineLength = 15;
		NGram trigrams = new NGram("WSJ02-21.pos", 3);
		NGram bigrams = new NGram("WSJ02-21.pos", 2);
		NGram dictionaryCreator = new NGram("WSJ02-21.pos");
		Smoothing smoother = new Smoothing();
		HashMap<ArrayList<String[]>, ArrayList<String[]>> results = new HashMap<ArrayList<String[]>, ArrayList<String[]>>();
		HashMap<String, Integer> trigramsOfPosTags= trigrams.computeNGramsPosTag();
		HashMap<String, Integer> bigramsOfPosTags= bigrams.computeNGramsPosTag();
		int totalSentencesInTrainingCorpus = trigrams.getTotalSentences();
		HashMap<String, Double> postagTrigramPossibilities = smoother.goodTuringPos(trigramsOfPosTags, bigramsOfPosTags, this.k, totalSentencesInTrainingCorpus);
		
		Map<String, Map<String, Integer>> postagDictionary = dictionaryCreator.createPosTagDictionaryWithWordsAndCount();
		HashMap<String, HashMap<String, Double>> posTagDictionaryWithWordsAndPoss = smoother.goodTuringPosTagsCalcPossibilities(postagDictionary);
		
		ArrayList<String[]> nextLine = this.manager.readNextSentence();
		
		
		while(nextLine.size() > maxLineLength){
			nextLine = this.manager.readNextSentence();
		}
		
		//System.out.println(Arrays.deepToString(nextLine.toArray()));
		try{
			FileWriter fstream = new FileWriter("resultsOfTagger.txt");
			BufferedWriter out = new BufferedWriter(fstream);
		
		while(nextLine != null){
			//System.out.println("Tagging sentence: " + Arrays.deepToString(nextLine.toArray()));
			if(nextLine.size() == 0)
			{
				nextLine = this.manager.readNextSentence();
				while(nextLine.size() > maxLineLength){
					nextLine = this.manager.readNextSentence();
				
				}
				continue;
			}
			String sentence = "<s> <s> ";
			for(String[] elem : nextLine){
				sentence += elem[0] + " ";
			}
			sentence = sentence.substring(0, sentence.length()-1);
			
			String[] sentenceTrigrams = dictionaryCreator.createNGramsOfSentence(sentence.split(" "), 3);
			
			ArrayList<String[]> sentenceTagged = createPosTagSequenceForSentence(sentenceTrigrams, postagTrigramPossibilities, posTagDictionaryWithWordsAndPoss);
			//System.out.println("Result of Tagging: " + Arrays.deepToString(sentenceTagged.toArray()));
			//System.out.println("\n \n \n");
			results.put(nextLine, sentenceTagged);
			out.write("O - " + Arrays.deepToString(nextLine.toArray())+ "\n" + "T - " + Arrays.deepToString(sentenceTagged.toArray()) + "\n\n");
			nextLine = this.manager.readNextSentence();
			
			while(nextLine != null && nextLine.size() > maxLineLength){
				nextLine = this.manager.readNextSentence();
			}
			
		}

			out.close();
		} catch (Exception e){
			System.err.println("Error: " + e.getMessage());
		}
		
		correctnessOfTaggerForSentence(results);
	}
	
	public ArrayList<String[]> createPosTagSequenceForSentence(String[] sentenceTrigrams, HashMap<String, Double> postagTrigramsPoss, HashMap<String, HashMap<String, Double>> posTagDictionaryWithWordsAndPoss){
		
		double viterbiPoss = 1;
		double bestValue = 0;
		String bestTag = "";
		String bigram = "";
		String currentTag = "";
		
		ArrayList<String[]> taggedSentence = new ArrayList<String[]>();
		String[] startSentence = sentenceTrigrams[0].split(" ");
		String[] predecessorBigram = {startSentence[0], startSentence[1]};
		
		for(String trigram : sentenceTrigrams){
			HashMap<String, Double> postagsWithPossGivenPredecessors = new HashMap<String, Double>();
			HashMap<String, Double> wordGivenPostagPossibilities = new HashMap<String, Double>();
			HashMap<String, Double> intermediateViterbiPossMap = new HashMap<String, Double>();
		
		
			//System.out.println(trigram);
			postagsWithPossGivenPredecessors.clear();
			wordGivenPostagPossibilities.clear();
			intermediateViterbiPossMap.clear();
			
			//System.out.println("======= Checking Tag For " + trigram + "=======");
			//System.out.println(postagsWithPossGivenPredecessors);
			//System.out.println(wordGivenPostagPossibilities);
			//System.out.println(intermediateViterbiPossMap);
			
			String[] trigramArray = trigram.split(" ");
			String wordToTag = trigramArray[2];
			//System.out.println(Arrays.toString(trigramArray));
			//System.out.println(Arrays.toString(predecessorBigram));
			/**
			Deze for loop haalt alle mogelijke postags op gegeven de twee voorgangers, en de kans daarop. En stopt deze in de hashmap postagsWithPossGivenPredecessors.
			**/
			
			for(Map.Entry<String, Double> elem : postagTrigramsPoss.entrySet()){
				String[] currentElement = elem.getKey().split(" ");
				//System.out.println(Arrays.toString(currentElement));
				//System.out.println("Does elem 0 equal:" + trigramArray[0].equals(currentElement[0]));
				//System.out.println(currentElement[0] + " --- " + trigramArray[0]);
				if(predecessorBigram[0].equals(currentElement[0]) && predecessorBigram[1].equals(currentElement[1])){
					//System.out.println(Arrays.toString(trigramArray) + " -- " + Arrays.toString(currentElement));
					postagsWithPossGivenPredecessors.put(currentElement[2], elem.getValue());
				}
				
			}
			//System.out.println("Resultaat eerste loop:" + postagsWithPossGivenPredecessors);
			
			
			/**
			Deze for-loop haalt voor elke tag uit de vorige loop de mogelijke woorden op met hun kans gegeven die tag en zet deze in wordGivenPostagPossibilities. 
			Als het niet bekent is haalt hij de kans op dat het woord niet voorkomt bij deze tag.
			**/
			
			for(Map.Entry<String, Double> postagEntry : postagsWithPossGivenPredecessors.entrySet()){
				currentTag = postagEntry.getKey();
				//System.out.println(currentTag);
				Map<String, Double> wordsAndPossibilityForTag = posTagDictionaryWithWordsAndPoss.get(currentTag);
				//System.out.println(wordsAndPossibilityForTag);
				if(wordsAndPossibilityForTag.containsKey(wordToTag)){
					//System.out.println("IF - Current word: " + wordToTag + " --- " + wordsAndPossibilityForTag.get(wordToTag));
					wordGivenPostagPossibilities.put(currentTag, wordsAndPossibilityForTag.get(wordToTag));					
				}else{
					//System.out.println("ELSE - Current word: " + wordToTag + " --- " + wordsAndPossibilityForTag.get("0Count"));
					wordGivenPostagPossibilities.put(currentTag, wordsAndPossibilityForTag.get("0Count"));					
				}

			}
			//System.out.println(posTagDictionaryWithWordsAndPoss);
			
			//System.out.println("\n Resultaat tweede loop:" + wordGivenPostagPossibilities);
			
			/**
			Deze loop haalt de kansen uit de vorige loops en vermenigvuldigt deze met elkaar.
			**/
			
			for(Map.Entry<String, Double> postagWithPossGivenPre : postagsWithPossGivenPredecessors.entrySet()){
				String tag = postagWithPossGivenPre.getKey();
				double wordPoss = wordGivenPostagPossibilities.get(tag);
				double intermediateViterbiPoss = postagWithPossGivenPre.getValue() * wordPoss;
				intermediateViterbiPossMap.put(tag, intermediateViterbiPoss);
			}
			
			//System.out.println("\n Resultaat derde loop: " + intermediateViterbiPossMap);
			
			/**
			Deze loop selecteert de hoogste waarde uit het product van de vorige loop.
			**/
			for(Map.Entry<String, Double> intermediatePoss : intermediateViterbiPossMap.entrySet()){
				if(bestValue < intermediatePoss.getValue()){
					bestValue = intermediatePoss.getValue();
					bestTag = intermediatePoss.getKey();
				}
			}
			predecessorBigram[0] = predecessorBigram[1];
			predecessorBigram[1] = bestTag;
			String[] temp = {wordToTag, bestTag};
			taggedSentence.add(temp);
			viterbiPoss *= bestValue;
			bestValue = 0;
			bestTag = "";
			
			//System.out.println(postagsWithPossGivenPredecessors.size());
			//System.out.println(wordGivenPostagPossibilities.size());
			//System.out.println(intermediateViterbiPossMap.size());
		
		}
		//String[] endresult = {"Possibility for this postag sequence", "" + viterbiPoss};
		//taggedSentence.add(endresult);
		//System.out.println("The final tagged sentence: " + Arrays.deepToString(taggedSentence.toArray()));
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
	

	/**
	* calculatePosTagDictionaryProbabilities(HashMap<String, HashMap<String, Double>> dictionary) calculates the probabilities 
	* of the words given their tag.
	**/
	public HashMap<String, HashMap<String, Double>> calculatePosTagDictionaryProbabilities(HashMap<String, HashMap<String, Double>> dictionary){
		HashMap<String, HashMap<String, Double>> dictionaryPosTagProbabilities = new HashMap<String, HashMap<String, Double>>();
		
		for(Map.Entry<String, HashMap<String, Double>> entry : dictionary.entrySet()){
			HashMap<String, Double> innerMap = new HashMap<String, Double>();
			double totalWordsOfTag = totalWordCountSmoothed(entry.getValue()) + 1;
			for(Map.Entry<String, Double> innerEntry: entry.getValue().entrySet()){
				double probability = innerEntry.getValue()/totalWordsOfTag;
				innerMap.put(innerEntry.getKey(), probability);
			}
			innerMap.put("z3r0Pr0b", 1/totalWordsOfTag);
			dictionaryPosTagProbabilities.put(entry.getKey(),innerMap);
		}
		
		return dictionaryPosTagProbabilities;
	}
	
	/**
	* findBestTag(Map<String, Integer> tagsCount) finds the best possible tag-value pair
	* from a Map of possible tags and returns the tag with the highest count.
	**/
	public String findBestTag(Map<String, Integer> tagsCount){
		Map.Entry<String, Integer> maxEntry = null;

		for (Map.Entry<String, Integer> entry : tagsCount.entrySet())
		{
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
			{
				maxEntry = entry;
			}
		}
		return maxEntry.getKey();
	}
	
	public int totalWordCount(Map<String, Integer> tagsCount){
		int total = 0;
		for(Map.Entry<String, Integer> elem : tagsCount.entrySet()){
			total += elem.getValue();
		}
		return total;
	}
	
	public double totalWordCountSmoothed(Map<String, Double> tagsCount){
		double total = 0;
		for(Map.Entry<String, Double> elem : tagsCount.entrySet()){
			total += elem.getValue();
		}
		return total;
	}

	public void addToMap(String sentence, double prob){
		sortedSentences.put(prob, sentence);
	}
	
	
	public void printTopTwoProbabilities(){
	
		NavigableMap<Double, String> reversed = sortedSentences.descendingMap();
	
		int i=0;
		
		Iterator entries = reversed.entrySet().iterator();
		while(i < 2 && entries.hasNext()){
			Map.Entry entry = (Map.Entry) entries.next();
			double prob = new Double(entry.getKey().toString());
			if(prob > 0.0){
				System.out.printf("Probability for: '%s' is: %e \n", entry.getValue(), entry.getKey());
			}else{
				System.out.println("The probabilities for (the rest of) the sentences are zero");
			}
			i++;
		}
	}
	
}

