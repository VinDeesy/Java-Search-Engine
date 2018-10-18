import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

// TODO Refactor to InvertedIndex

/**
 * Data structure to store strings and their positions.
 */
public class wordIndex {
	// TODO private
	/**
	 * Stores a mapping of words to the positions the words were found.
	 */
	public final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	
	private Integer position; // TODO Could remove

	/**
	 * Initializes the index.
	 */
	public wordIndex() {
		this.index = new TreeMap<>();
		this.position = 1;

	}

	/* TODO More general
	public boolean add(String word, String fileName, int position) {
		
	}
	*/
	
	/**
	 * Adds the word and the position it was found to the index.
	 *
	 * @param word     word to clean and add to index
	 * @param fileName file word is located in
	 * @return true if this index did not already contain this word and position
	 */
	public boolean add(String word, String fileName) {

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

		
		
		/* TODO
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(fileName, new TreeSet<Integer>());
		return index.get(word).get(fileName).add(position);
		*/
	}

	/**
	 * Adds the array of words at once, assuming the first word in the array is at
	 * position 1.
	 *
	 * @param words    array of words to add
	 * @param fileName path to file location where words are located
	 * @return true if this index is changed as a result of the call (i.e. if one or
	 *         more words or positions were added to the index)
	 *
	 * @see #addAll(String[], String)
	 */
	public boolean addAll(String[] words, Path fileName) {

		return addAll(words, fileName.toString());
	}

	/**
	 * Adds the array of words at once, assuming the first word in the array is at
	 * the provided starting position
	 *
	 * @param words    array of words to add
	 * @param fileName name of the file the words are located in
	 * @return true if this index is changed as a result of the call (i.e. if one or
	 *         more words or positions were added to the index)
	 */
	public boolean addAll(String[] words, String fileName) {

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
	public int count(String word) { // TODO locations
		// TODO if index.get(word) is null, should reuturn 0
		return index.get(word).size();
	}

	/**
	 * Returns the number of words stored in the index.
	 *
	 * @return number of words
	 */
	public int words() {

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

	/* TODO 
	public boolean contains(String word, String path)
	public boolean contains(String word, String path, int position)
	*/

	/**
	 * Returns a string representation of this index.
	 */
	@Override
	public String toString() {
		return this.index.toString();
	}

	/* TODO
	public void toJSON(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);) {
			TreeJSONWriter.asNestedObject(index, writer, 1);
		} 
	}
	*/
	
	/*
	 * TODO Move this to an InvertedIndexBuilder
	 * 
	 * Create a method that gets the list of text files, loops, and calls search on all of them
	 * public static void addFiles(Path root, InvertedIndex index) {
	 * 		ArrayList<Path> pathList = traverser.getPaths();

			for (Path path : pathList) {
				addFile(path, index);
			}
	 * }
	 * 
	 * public static void addFile(Path path, InvertedIndex index)
	 */
	/**
	 * Searches the file and adds the words based upon their position and file
	 * location
	 *
	 * @param path location of file to search
	 * @return null
	 * 
	 */

	public void search(Path path) {
		// TODO int position = 1;
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);)

		{

			String line = null;
			
			/*
			 * TODO 
			 * Hidden efficiency issues
			 * 
			 * 1) Creating 1 stemmer per line, lots of objects getting created
			 * that must be cleaned up by the Java garbage collector
			 * 
			 * Create a stemmer in here, and reuse for all the lines
			 * 
			 * 2) Any time you use temporary storage you end up using more space
			 * and time than necessary....
			 * 
			 * stemLine adds to a list, you loop through the list, and add to the index
			 * 
			 * Efficiency is one reason to create more specific versions of generalized code
			 * 
			 * Still need to call parse() but instead of adding to a list immediately add to your index
			 */

			while ((line = reader.readLine()) != null) {

				List<String> stemmed = TextFileStemmer.stemLine(line);

				addAll(stemmed.toArray(new String[stemmed.size()]), path);

			}
			position = 1;

		} catch (IOException e) {
			e.printStackTrace(); // TODO No stack traces

		}

	}

}
