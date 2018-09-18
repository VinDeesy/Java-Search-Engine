import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.nio.file.Path;
/**
 * Data structure to store strings and their positions.
 */
public class wordIndex {

	/**
	 * Stores a mapping of words to the positions the words were found.
	 */
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	public int position;
	/**
	 * Initializes the index.
	 */
	public wordIndex() {
		this.index = new TreeMap<>();
		this.position = 1;
		
	}

	/**
	 * Adds the word and the position it was found to the index.
	 *
	 * @param word     word to clean and add to index
	 * @param position position word was found
	 * @return true if this index did not already contain this word and position
	 */
	public boolean add(String word, String fileName) {
		
	//	System.out.println("Adding: " + word);
		
		if (!index.containsKey(word)) {
			
			index.putIfAbsent(word, new TreeMap<>());
			index.get(word).put(fileName, new TreeSet<Integer>());
			index.get(word).get(fileName).add(position);
			
			return true;
		}
		
		else if (!index.get(word).containsKey(fileName)) {
			index.get(word).put(fileName, new TreeSet<Integer>());
			index.get(word).get(fileName).add(position);
		}
		
		// index.get returns a boolean!
		
		return index.get(word).get(fileName).add(position);
		
		
		
		
	}

	/**
	 * Adds the array of words at once, assuming the first word in the array is at
	 * position 1.
	 *
	 * @param words array of words to add
	 * @return true if this index is changed as a result of the call (i.e. if one
	 *         or more words or positions were added to the index)
	 *
	 * @see #addAll(String[], int)
	 */
	public boolean addAll(String[] words, Path fileName) {
	
		
		return addAll(words, fileName.toString());
	}

	/**
	 * Adds the array of words at once, assuming the first word in the array is at
	 * the provided starting position
	 *
	 * @param words array of words to add
	 * @param start starting position
	 * @return true if this index is changed as a result of the call (i.e. if one
	 *         or more words or positions were added to the index)
	 */
	public boolean addAll(String[] words, String fileName) {
		/*
		 * TODO: Add each word using the start position. (You can call your other
		 * methods here.)
		 */
		
		Boolean added = false;
		
		for (String word : words) {
			added = add(word, fileName);
			position++;
		}
	
		return added;
	}

	/**
	 * Returns the number of times a word was found (i.e. the number of positions
	 * associated with a word in the index).
	 *
	 * @param word word to look for
	 * @return number of times the word was found
	 */
	public int count(String word) {
		/*
		 * TODO: Return the count.
		 */
		return index.get(word).size();
	}

	/**
	 * Returns the number of words stored in the index.
	 *
	 * @return number of words
	 */
	public int words() {
		/*
		 * TODO: Return number of words. No counting is necessary!
		 */
		return index.size();
	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word word to look for
	 * @return true if the word is stored in the index
	 */
	public boolean contains(String word) {

		return index.containsKey(word);
	}

	/**
	 * Tests whether the index contains the specified word at the specified
	 * position.
	 *
	 * @param word     word to look for
	 * @param position position to look for word
	 * @return true if the word is stored in the index at the specified position
	 */
	public boolean contains(String word, int position) {

		
		try {
			return index.get(word).containsKey(position);
		} catch (NullPointerException e) {

		//	e.printStackTrace();
			return false;
		}

	}

	/**
	 * Returns a copy of the words in this index as a sorted list.
	 *
	 * @return sorted list of words
	 *
	 * @see ArrayList#ArrayList(java.util.Collection)
	 * @see Collections#sort(List)
	 */
	public ArrayList<String> copyWords() {
		/*
		 * TODO: Create a copy of the words in the index as a list, and sort before
		 * returning.
		 */
		
		ArrayList<String> list = new ArrayList<>();
		
		for (String word : index.keySet()) 
			list.add(word);
		

		Collections.sort(list);
		return list;
		
	}
	

	/**
	 * Returns a copy of the positions for a specific word.
	 *
	 * @param word to find in index
	 * @return sorted list of positions for that word
	 *
	 * @see ArrayList#ArrayList(java.util.Collection)
	 * @see Collections#sort(List)
	 */
	public ArrayList<Integer> copyPositions(String word, String fileName) {
		/*
		 * TODO: Create a copy of the positions for the word, and sort before
		 * returning.
		 */
		
		ArrayList<Integer> list = new ArrayList<>();
		
		for (int position : index.get(word).get(fileName)) 
			list.add(position);
		
		Collections.sort(list);
		return list;
	}

	/**
	 * Returns a string representation of this index.
	 */
	@Override
	public String toString() {
		return this.index.toString();
	}
	
	public void print() {
		
		
		
		
	}
	
}
