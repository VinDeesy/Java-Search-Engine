import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
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
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	protected final TreeMap<String, Integer> locations; // TODO private

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
		return index.get(word).get(fileName).add(position);

		/*
		 * TODO Here, every time you see fileName, increase the word count by 1
		 * 
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(fileName, new TreeSet<Integer>());
		boolean result = index.get(word).get(fileName).add(position);
		
		if (result) {
		 	update the location map here
		}
		
		return result;
		 */
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
	 * TODO Forgot to javadoc, also... remove!
	 * @param location
	 * @param count
	 */
	public void addLocation(String location, Integer count) {
		locations.put(location, count);
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
	public ArrayList<Result> searchExact(TreeSet<String> query) { // TODO Collection<String>
		// TODO Remove the try/catch
		try {

			ArrayList<Result> results = new ArrayList<>();
			Map<String, Result> lookup = new TreeMap<>(); // TODO HashMap

			for (String word : query) {

				if (index.containsKey(word)) {

					for (Entry<String, TreeSet<Integer>> file : index.get(word).entrySet()) {

						if (!lookup.containsKey(file.getKey())) {
							Result result = new Result(file.getValue().size(), file.getKey(),
									locations.get(file.getKey()));
							results.add(result);
							lookup.put(file.getKey(), result);
						} else {
							lookup.get(file.getKey()).updateCount(file.getValue().size());
						}

					}

				}

			}
			Collections.sort(results);
			return results;

		} catch (Exception e) {
			System.out.println("There was an error with searching the index");
		}
		return null;

	}

	/**
	 * Searches the index for partial matches from a list of queries
	 *
	 * @param queries query words to search our index
	 * @return ArrayList of results
	 */
	public ArrayList<Result> searchPartial(TreeSet<String> query) {

		try {

			ArrayList<Result> results = new ArrayList<>();
			Map<String, Result> lookup = new TreeMap<>();

			/*
			 * First, you need to swap the loops
			 * first loop through query words
			 * then loop through index entries
			 * 
			 * if we can start in the "right" place then as soon as we find a key
			 * that no longer starts with our query we can break out of our loop
			 * 
			 * to start in the right place, look at what happens when you give
			 * tailMap or headMap something that isn't a key in your map!
			 * choose the one that makes sense for this problem
			 * 
			 * https://github.com/usf-cs212-fall2018/lectures/blob/master/Data%20Structures/src/FindDemo.java
			 */
			
			for (Entry<String, TreeMap<String, TreeSet<Integer>>> indexWord : index.entrySet()) {

				for (String word : query) {

					if (indexWord.getKey().startsWith(word)) {

						// TODO Pull out this for loop into a private void searchHelper(entry, list, lookup)
						for (Entry<String, TreeSet<Integer>> file : index.get(indexWord.getKey()).entrySet()) {

							if (!lookup.containsKey(file.getKey())) {
								Result result = new Result(file.getValue().size(), file.getKey(),
										locations.get(file.getKey()));
								results.add(result);
								lookup.put(file.getKey(), result);
							} else {
								lookup.get(file.getKey()).updateCount(file.getValue().size());
							}

						}

					}

				}

			}

			Collections.sort(results);
			return results;

		} catch (Exception e) {
			System.out.println("There was probably an error with the path");

		}
		return null;

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
	 * 
	 */
	public void locationJSON(Path location) {
		try (BufferedWriter writer = Files.newBufferedWriter(location, StandardCharsets.UTF_8);) {
			TreeJSONWriter.printLocations(locations, writer);
		} catch (IOException e) { // TODO throw exception to Driver

			System.out.println("Error!");
		}

	}
}
