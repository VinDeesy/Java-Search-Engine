import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
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
	private final TreeMap<String, Integer> locations;

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

	public TreeMap<String, ArrayList<Result>> searchExact(ArrayList<TreeSet<String>> queries) {

		try {

			TreeMap<String, ArrayList<Result>> resultMap = new TreeMap<>();
			for (TreeSet<String> query : queries) {
				boolean found = false;
				ArrayList<Result> results = new ArrayList<>();
				String queryName = String.join(" ", query);
				TreeMap<String, Result> lookup = new TreeMap<>();

				for (String word : query) {

					if (index.containsKey(word)) {
						found = true;
						for (Entry<String, TreeSet<Integer>> fileEntry : index.get(word).entrySet()) {

							if (!lookup.containsKey(fileEntry.getKey())) {
								Result result = new Result(fileEntry.getValue().size(), queryName, fileEntry.getKey(),
										locations.get(fileEntry.getKey()));
								results.add(result);
								lookup.put(fileEntry.getKey(), result);
							} else {
								lookup.get(fileEntry.getKey()).updateCount(fileEntry.getValue().size());
							}

						}

					}
					if (!found) {
						Result result = null;
						results.add(result);
					}

				}
				Collections.sort(results);
				resultMap.put(queryName, results);
			}

			for (Entry<String, ArrayList<Result>> query : resultMap.entrySet()) {

				System.out.println(query.getKey());

				for (Result result : query.getValue()) {

					System.out.println(result.file + " " + result.count + " " + result.query);

				}

			}

			return resultMap;

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
	public TreeMap<String, ArrayList<Result>> searchPartial(ArrayList<TreeSet<String>> queries) {

		try {

			ArrayList<Result> results = new ArrayList<>();
			TreeMap<String, ArrayList<Result>> resultMap = new TreeMap<>();
			for (Entry<String, TreeMap<String, TreeSet<Integer>>> indexWord : index.entrySet()) {

				for (TreeSet<String> query : queries) {
					boolean found = false;
					String queryName = String.join(" ", query);
					TreeMap<String, Result> lookup = new TreeMap<>();
					for (String queryWord : query) {

						if (indexWord.getKey().startsWith(queryWord)) {
							found = true;
							for (Entry<String, TreeSet<Integer>> fileEntry : index.get(queryWord).entrySet()) {

								if (!lookup.containsKey(fileEntry.getKey())) {
									Result result = new Result(fileEntry.getValue().size(), queryName,
											fileEntry.getKey(), locations.get(fileEntry.getKey()));
									results.add(result);
									lookup.put(fileEntry.getKey(), result);
								} else {
									lookup.get(fileEntry.getKey()).updateCount(fileEntry.getValue().size());
								}

							}

						}

						if (!found) {
							Result result = null;
							results.add(result);
						}

					}
					Collections.sort(results);
					resultMap.put(queryName, results);
				}

			}
			return resultMap;
		} catch (Exception e) {
			System.out.println("There was probably an error with the path");
			return null;
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
	 * 
	 */
	public void locationJSON(Path location) {
		try (BufferedWriter writer = Files.newBufferedWriter(location, StandardCharsets.UTF_8);) {
			TreeJSONWriter.printLocations(locations, writer);
		} catch (IOException e) {

			System.out.println("Error!");
		}

	}
}
