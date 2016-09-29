/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * <Ashvin Roharia>
 * <ar34426>
 * <16475>
 * <Minkoo Park>
 * <mp32454>
 * <16480>
 * Slip days used: <0>
 * Git URL: https://github.com/aroharia/hw3
 * Fall 2016
 */

package assignment3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class Main {
	
	// static variables and constants only here.
	public static Scanner kb;	// input Scanner for commands
	public static ArrayList<String> start_end_wordList; //array list containing start word and end word
	public static String startWord; //first word for the word ladder
	public static String endWord; //last word for the word ladder
	public static Set<String> dictionaryWords; //all the words in the given dictionary
	
	public static void main(String[] args) throws Exception {
		
		
		PrintStream ps;	// output file
		// If arguments are specified, read/write from/to files instead of Std IO.
		if (args.length != 0) {
			kb = new Scanner(new File(args[0]));
			ps = new PrintStream(new File(args[1]));
			System.setOut(ps);			// redirect output to ps
		} else {
			kb = new Scanner(System.in);// default from Stdin
			ps = System.out;			// default to Stdout
		}
		
		// Reads user input and sets startWord and endWord
		initialize();
		
		//Ashvin is working on currently
		ArrayList<String> testingBFS = getWordLadderBFS(startWord, endWord);
		
		//TO DO
		printLadder(testingBFS);
		
		//TO DO
		//ArrayList<String> testingDFS = getWordLadderDFS(startWord, endWord);
		
		//TO DO
		//printLadder(testingDFS);

	}
	
	public static void initialize() {
		// initialize your static variables or constants here.
		// We will call this method before running our JUNIT tests.  So call it 
		// only once at the start of main.
		dictionaryWords = makeDictionary();
		start_end_wordList = parse(kb);
		startWord = start_end_wordList.get(0).toString();
		endWord = start_end_wordList.get(1).toString();
		
		
	}
	
	/**
	 * @param keyboard Scanner connected to System.in
	 * @return ArrayList of 2 Strings containing start word and end word. 
	 * If command is /quit, return empty ArrayList. 
	 */
	public static ArrayList<String> parse(Scanner keyboard) {
		System.out.println("Enter two words here:");
		String input = keyboard.nextLine();
		
		//Check if "/quit" was entered
		if (input.equals("/quit"))
			return null;
		
		//ArrayList<String> start_end_wordList = (ArrayList<String>) Arrays.asList(input.split(" "));
		ArrayList<String> start_end_wordList = new ArrayList<String>(Arrays.asList(input.split(" ")));
		return start_end_wordList;
	}
	
	/**
	 * Finds the word ladder of 2 words through DFS using recursion
	 * @param start is the first word as a String
	 * @param end is the last word as a String
	 * @return ArrayList of the ladder or null if it doesn't exist
	 */
	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		// initialize dictionary set
		Set<String> dict = makeDictionary();
		// to make it comparable to dictionary words
		start = start.toUpperCase();
    	end = end.toUpperCase();
		// no word ladder if first/last word are not in dictionary
		if (!(dict.contains(start) && dict.contains(end)))
			return null;
    	// no word ladder if first word = last word
    	if (start.equals(end))
			return null;
		 	
    	ArrayList<String> result = computeLadder(start, end, dict); 
	 	
		// create reversed ladder for result
		ArrayList<String> reverseLadder = new ArrayList<String>();
		// if no path to create ladder, return null
		if(result == null)
			return null;
		for (int i = result.size()-1; i >= 0; i -= 1) { 
			String word = result.get(i).toLowerCase();
			reverseLadder.add(word);	
		}
		return reverseLadder;
	}
	
	/**
	 * Finds the word ladder of 2 words through breadth first search
	 * @param start is the first word as a String
	 * @param end is the last word as a String
	 * @return ArrayList of the ladder or null if it doesn't exist
	 */
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
		// dict holds all the words in the given dictionary
		Set<String> dict = makeDictionary();
		
		// to make it comparable to dictionary words
		start = start.toUpperCase();
    	end = end.toUpperCase();
    	
		// no word ladder if first/last word are not in dictionary
		if (!(dict.contains(start) && dict.contains(end)))
			return null;
    	
    	// no word ladder if first word = last word
    	if (start.equals(end))
			return null;
		
		// Queue
		Queue<String> queue = new LinkedList<String>();
		queue.add(start);
		
		//  map
		Map<String,String> parentWordMap = new HashMap<String,String>(dict.size());
		parentWordMap.put(start, null);

		// discovered set
		Set<String> discovered = new HashSet<String>(dict.size());
		discovered.add(start);
		
		StartSearch : // label for outer loop to break to
		// while there exists more undiscovered words
		while (!queue.isEmpty()) {
			String current = queue.remove();
			// goes through the word letter by letter (gets length by checking how long the dictionary words are)
			for (int i=0; i<dict.iterator().next().length(); i++)
				// checks all letters A-Z
				for (char j='A'; j<='Z'; j++)
					// if the letter being 
					if (j ==current.charAt(i))
						continue;
					else {
						// possible new word
						String next = current.substring(0,i) + String.valueOf(j) + current.substring(i+1,dict.iterator().next().length());
						// if not already discovered
						if (!discovered.contains(next)){
							// if the newly generated word is the end word, ladder has been found
							if (next.equals(end)) {
								parentWordMap.put(end,current);
								break StartSearch; //breaks out of the outer loop to leave search
							// if the newly generated word exists, add to queue
							} else if (dict.contains(next)){
								queue.add(next);
								discovered.add(next);
								parentWordMap.put(next,current);
							}
						}
					}
		}
		
		// return null if ladder does not exist
		if (!parentWordMap.containsKey(end))
			return null;

		// stack to get the ladder (in a reversed order)
		Stack<String> reversedLadder = new Stack<String>();
		reversedLadder.add(end);
		String current = end;
		while ((current = parentWordMap.get(current)) != null){
			reversedLadder.add(current);
		}
		
		// create a new array list to reverse ladder into
		ArrayList<String> finalLadder = new ArrayList<String>(reversedLadder.size());
		while (!reversedLadder.isEmpty()){
			finalLadder.add(reversedLadder.pop().toLowerCase());
		}
		
		return finalLadder;
	}
    
	/**
	 * Takes all the words from a dictionary text file and converts into a Set<String>
	 * @return Set of all the words in the given dictionary
	 */
	public static Set<String>  makeDictionary () {
		Set<String> words = new HashSet<String>();
		Scanner infile = null;
		try {
			infile = new Scanner (new File("five_letter_words.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Dictionary File not Found!");
			e.printStackTrace();
			System.exit(1);
		}
		while (infile.hasNext()) {
			words.add(infile.next().toUpperCase());
		}
		return words;
	}
	
	/**
	 * Prints the word ladder in the appropriate format
	 * @param ladder is the ArrayList of words in the ladder, or null if it doens't exist
	 */
	public static void printLadder(ArrayList<String> ladder) {
		// if no ladder was found
		if (ladder == null){
			System.out.println("no word ladder can be found between " + startWord + " and " + endWord + ".");
			return;
		}
		// if a ladder was found
		else {
			System.out.println("a " + (ladder.size()-2) + "-rung word ladder exists between " + startWord + " and " + endWord);
			for(int i = 0; i < ladder.size(); i++){
				System.out.println(ladder.get(i));
			}
		}
	}
	// TODO
		 /**
     * computeLadder(start,end,dictionary) returns the word
     * ladder from start to end using recursion
     * @param   String start   The beginning of the word ladder
     * @param   String end     The end of the word ladder
     * @param   Set<String> dict  The list of all valid words
     *
     */
	private static ArrayList<String> computeLadder(String start, String end, Set<String> dict) {
		
		ArrayList<String> neighbors = findNeighbors(start, dict);
		
		// if no neighbor at all, no ladder possible
		if (neighbors.isEmpty()) { 
				return null; 
		}
		else if (neighbors.contains(end)) {
		// if only one edge apart, return start and end e.g.) prone-drone
			ArrayList<String> path = new ArrayList<String>();
			String  topLadder= end;	path.add(topLadder);			
			String  bottomLadder= start;path.add(bottomLadder);
			return path;
		}
		
		ArrayList<String> shortPath = new ArrayList<String>();
		int shortLen = dict.size();
		String shorter = null;
		shortPath = null;
		// remove visited words
		dict.removeAll(neighbors);
		
		// recursively traverse the graph
		for (String w : neighbors) {
			// access all unvisited neighboring nodes
			ArrayList<String> path = computeLadder(w,end,dict);
	        // compute and compare path's length	
			if (!(path == null)) {	
				if (path.size() < shortLen) {
					shortPath = path;
					shortLen = path.size();
					shorter = w;
				}
			} 
			else { continue; }
		}
		// if no path for ladder, 
		if (shortPath == null) { 
			return null;
		}		else{ 
					dict.add(shorter);
					shortPath.add(start);
					return shortPath;			
		}

	}

}
