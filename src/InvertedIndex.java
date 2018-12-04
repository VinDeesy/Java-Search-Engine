import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure to store strings and their positions.
 */
public class InvertedIndex {

	/**
	 * Stores a mapping of words to the positions the words were found.
	 */
	// TODO make these private again
	public final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	public final TreeMap<String, Integer> locations;

	/**
	 * Initializes the index.
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
		this.locations = new TreeMap<>();
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
		boolean result = index.get(word).get(fileName).add(position);

		if (result) {

			locations.put(fileName, Math.max(locations.getOrDefault(fileName, 0), position));
			locations.put(fileName, position);
		}

		return result;

	}

	/**
	 * Merges a local index into the main index
	 *
	 * @param local index to merge
	 * @return none
	 */
	public void addAll(InvertedIndex local) {
		for (String word : local.index.keySet()) {

			if (this.index.containsKey(word) == false) {
				this.index.put(word, local.index.get(word));
			} else {
				for (String path : local.index.get(word).keySet()) {
					if (!this.index.get(word).keySet().contains(path)) {
						this.index.get(word).put(path, local.index.get(word).get(path));
					} else {
						this.index.get(word).get(path).addAll(local.index.get(word).get(path));
					}
				}
			}
		}

		for (String location : local.locations.keySet()) {
			if (!this.locations.containsKey(location)) {
				this.locations.put(location, local.locations.get(location));
			} else {
				this.locations.put(location, local.locations.get(location) + this.locations.get(location));
			}

		}
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

		return index.get(word) == null ? false : index.get(word).containsKey(path);
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
			contains = index.get(word).get(path).contains(position);
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
	 * Searches the index for exact matches from a list of queries
	 *
	 * @param queries query words to search our index
	 * @return TreeMap of results
	 */
	public ArrayList<Result> searchExact(Collection<String> query) {

		ArrayList<Result> results = new ArrayList<>();
		Map<String, Result> lookup = new HashMap<>();

		for (String word : query) {

			if (index.containsKey(word)) {
				searchHelper(word, results, lookup);
			}

		}
		Collections.sort(results);
		return results;

	}

	/**
	 * Searches the index for partial matches from a list of queries
	 *
	 * @param queries query words to search our index
	 * @return ArrayList of results
	 */
	public ArrayList<Result> searchPartial(Collection<String> query) {

		ArrayList<Result> results = new ArrayList<>();
		Map<String, Result> lookup = new HashMap<>();

		for (String queryWord : query) {

			for (String indexWord : index.tailMap(queryWord).keySet()) {
				if (indexWord.startsWith(queryWord)) {
					searchHelper(indexWord, results, lookup);
				} else {
					break;
				}
			}
		}

		Collections.sort(results);
		return results;

	}

	/**
	 * Helper method to iterate through found words in index
	 *
	 * @param word    word to search index
	 * @param results list of results
	 * @param lookup  map containing results we have already created
	 * @return none
	 */
	private void searchHelper(String word, ArrayList<Result> results, Map<String, Result> lookup) {

		for (Entry<String, TreeSet<Integer>> file : index.get(word).entrySet()) {

			if (!lookup.containsKey(file.getKey())) {
				Result result = new Result(file.getValue().size(), file.getKey(), locations.get(file.getKey()));
				results.add(result);
				lookup.put(file.getKey(), result);
			} else {
				lookup.get(file.getKey()).updateCount(file.getValue().size());
			}

		}
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

	/**
	 * Prints the locations and word counts for each file in the index
	 * 
	 * @param path location to output JSON data
	 * @return null
	 * @throws IOException
	 * 
	 */
	public void locationJSON(Path location) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(location, StandardCharsets.UTF_8);
		{
			TreeJSONWriter.printLocations(locations, writer);
			writer.close();
		}

	}
}