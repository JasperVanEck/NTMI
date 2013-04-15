NTMI
====

Opdrachten voor NTMI



How to run the different programs:

First off, all the classes can be compiled with: 'javac *.java'

Several parts of the program can be run, by (un)commenting certain lines in Main.java.

When you want to run the program after compilation, you can call it with:
java Main -n <int> -m <int> -f <corpusfilename> -af <additionalfilename>

-n	Is the n for the Ngrams. Default is 2.
-m	Is the number of top NGrams which you want to print.(use only when running NGram maker in Main) Default is 10.
-f	The corpus file, which is going to be used. Default is austen.txt
-af 	The additional file, which is used to read sentences from, to determine the probabilities of those sentences. 
	There is the option for probability_sentences.txt and Array-length(4/8).txt. The former contains a test sentence from the corpus, 
	to show the program works. The latter contains the permutations. 
	Default is probabilty_sentences.txt. 


Output/explanation of the program:
When using NGrammaker, the output will be the the top m frequencies of that NGram. 

When using the probabilityCalculator with probability_sentences.txt, the output will be the probability for the sentence in the .txt file.

When using the probabilityCalculator with Array-length(4/8).txt, the output will be the probabilties for each permutation of the A or B set.
Currently all these probabilities are 0, because the words in those sets aren't paired with an end symbol in the corpus. When the end symbol is not put at the end of the sentences we do get a probability. In set B, without the end symbol, the sentences "she was the youngest", is the most probable sentence of that set of permutations.


During the next part of the assignment, after smoothing has been implemented, this problem will be solved.

