import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data structure to store strings and their positions.
 */
public class InvertedThreaded extends InvertedIndex {

	/**
	 * Stores a mapping of words to the positions the words were found.
	 */

	Lock lock;

	/**
	 * Initializes the index.
	 */
	public InvertedThreaded() {
		this.lock = new Lock();
	}

	/**
	 * Adds the word and the position it was found to the index.
	 *
	 * @param word     word to clean and add to index
	 * @param fileName file word is located in
	 * @return true if this index did not already contain this word and position
	 */
	public boolean add(String word, String fileName, Integer position) {

		lock.lockReadWrite();
		try {
			return super.add(word, fileName, position);
		} finally {
			lock.unlockReadWrite();
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

		lock.lockReadOnly();
		try {
			return super.count(word);
		} finally {
			lock.unlockReadOnly();
		}
	}

	public void addLocation(String location, Integer count) {
		lock.lockReadWrite();
		try {
			super.addLocation(location, count);
		} finally {
			lock.unlockReadWrite();
		}
	}

	/**
	 * Returns the number of words stored in the index.
	 *
	 * @return number of words
	 */
	public int words() {
		lock.lockReadOnly();
		try {
			return super.words();
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word word to look for
	 * @return true if the word is stored in the index
	 */
	public boolean contains(String word) {
		lock.lockReadOnly();
		try {
			return super.contains(word);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word word to look for
	 * @param path path word is mapped to
	 * @return true if the word is stored in the index in the path
	 */
	public boolean contains(String word, String path) {

		lock.lockReadOnly();
		try {
			return super.contains(word, path);
		} finally {
			lock.unlockReadOnly();
		}
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

		lock.lockReadOnly();
		try {
			return super.contains(word, path, position);
		} finally {
			lock.unlockReadOnly();
		}

	}

	/**
	 * Searches the index for exact matches from a list of queries
	 *
	 * @param queries query words to search our index
	 * @return TreeMap of results
	 */

	public ArrayList<Result> searchExactThreaded(TreeSet<String> query, InvertedIndex index) {

		try {

			ArrayList<Result> results = new ArrayList<>();
			Map<String, Result> lookup = new TreeMap<>();

			for (String word : query) {

				if (index.index.containsKey(word)) {

					for (Entry<String, TreeSet<Integer>> file : index.index.get(word).entrySet()) {

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
			e.printStackTrace();
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
	public ArrayList<Result> searchPartialThreaded(TreeSet<String> query, InvertedIndex index) {

		try {

			ArrayList<Result> results = new ArrayList<>();
			Map<String, Result> lookup = new TreeMap<>();

			for (Entry<String, TreeMap<String, TreeSet<Integer>>> indexWord : index.index.entrySet()) {

				for (String word : query) {

					if (indexWord.getKey().startsWith(word)) {

						for (Entry<String, TreeSet<Integer>> file : index.index.get(indexWord.getKey()).entrySet()) {

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
			e.printStackTrace();
			System.out.println("There was probably an error with the path");

		}
		return null;

	}

}