import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure to store strings and their positions.
 */
public class InvertedIndex {

	/**
	 * Stores a mapping of words to the positions the words were found.
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	/**
	 * Initializes the index.
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();

	}

	/**
	 * Adds the word and the position it was found to the index.
	 *
	 * @param word     word to clean and add to index
	 * @param fileName file word is located in
	 * @return true if this index did not already contain this word and position
	 */
	public boolean add(String word, String fileName, Integer position) {

		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(fileName, new TreeSet<Integer>());
		return index.get(word).get(fileName).add(position);

	}

	/**
	 * Returns the number of times a word was found (i.e. the number of positions
	 * associated with a word in the index).
	 *
	 * @param word word to look for
	 * @return number of times the word was found
	 */
	public int count(String word) {

		return index.get(word) == null ? 0 : index.get(word).size();
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

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word word to look for
	 * @param path path word is mapped to
	 * @return true if the word is stored in the index in the path
	 */

	public boolean contains(String word, String path) {

		return index.get(word) == null ? false : index.get(word).containsKey(path.toString());
	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word     word to look for
	 * @param path     path word is mapped to
	 * @param position position in the file to look for
	 * @return true if the word is stored in the index
	 */

	public boolean contains(String word, String path, int position) {

		boolean contains = false;

		try {
			contains = index.get(word).get(path.toString()).contains(position);
		} catch (NullPointerException e) {
			return false;
		}
		return contains;

	}

	/**
	 * Returns a string representation of this index.
	 */
	@Override
	public String toString() {
		return this.index.toString();
	}

	/**
	 * Searches the file and adds the words based upon their position and file
	 * location
	 *
	 * @param path location to output JSON data
	 * @return null
	 * 
	 */

	public void toJSON(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);) {
			TreeJSONWriter.asNestedObject(index, writer, 1);
		}
	}

}
