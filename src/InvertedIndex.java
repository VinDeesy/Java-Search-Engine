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
	private final TreeMap<String, TreeMap<String, Integer>> resultMap;
	private Lock lock;

	/**
	 * Initializes the index.
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
		this.locations = new TreeMap<>();
		this.resultMap = new TreeMap<>();
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

		lock.lockReadWrite(); // Hey david i just wanted to say that you're not commenting your code correctly

		try {

			index.putIfAbsent(word, new TreeMap<>());
			index.get(word).putIfAbsent(fileName, new TreeSet<Integer>());
			return index.get(word).get(fileName).add(position);
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
			return index.get(word) == null ? 0 : index.get(word).size();
		} finally {
			lock.unlockReadOnly();
		}
	}

	public void addLocation(String location, Integer count) {
		lock.lockReadWrite();
		try {
			locations.put(location, count);
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
			return index.size();
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
			return index.containsKey(word);
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
			return index.get(word) == null ? false : index.get(word).containsKey(path);
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

			boolean contains = false;

			try {
				contains = index.get(word).get(path).contains(position);
			} catch (NullPointerException e) {
				return false;
			}
			return contains;
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Returns a string representation of this index.
	 */
	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return this.index.toString();
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Searches the index for exact matches from a list of queries
	 *
	 * @param queries query words to search our index
	 * @return ArrayList of results
	 */

	public ArrayList<ArrayList<Result>> searchExact(ArrayList<TreeSet<String>> queries,
			ArrayList<ArrayList<Result>> resultList) {

		lock.lockReadWrite();

		try {

			for (TreeSet<String> query : queries) {

				String queryName = String.join(" ", query);

				for (String word : query) {

					Integer count = 0;
					String fileName = "";

					TreeMap<String, TreeSet<Integer>> fileList = index.get(word);

					if (fileList != null) {

						for (Entry<String, TreeSet<Integer>> fileEntry : fileList.entrySet()) {

							count = fileEntry.getValue().size();
							fileName = fileEntry.getKey();

							if (resultMap.get(queryName) == null) {

								resultMap.put(queryName, new TreeMap<>());
								resultMap.get(queryName).put(fileName, count);

							} else if (resultMap.get(queryName).get(fileName) == null) {

								resultMap.get(queryName).put(fileName, count);

							} else {

								count = count + resultMap.get(queryName).get(fileName);

								resultMap.get(queryName).put(fileName, count);

							}

						}

						count = 0;

					} else {
						resultMap.putIfAbsent(queryName, null);
					}

				}

			}

			int i = 0;

			for (Entry<String, TreeMap<String, Integer>> q : resultMap.entrySet()) {

				resultList.add(new ArrayList<>());

				if (q.getValue() != null) {

					for (Entry<String, Integer> file : q.getValue().entrySet()) {

						double score = (double) file.getValue() / locations.get(file.getKey());

						Result result = new Result(file.getValue(), q.getKey(), file.getKey(), score);

						resultList.get(i).add(result);

					}

					Collections.sort(resultList.get(i));

					i++;
				} else {
					resultList.get(i).add(new Result(0, q.getKey(), null, 0));
					i++;
				}
			}

			return resultList;
		} catch (Exception e) {
			System.out.println("The path was probably null");
			return null;
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Searches the index for partial matches from a list of queries
	 *
	 * @param queries query words to search our index
	 * @return ArrayList of results
	 */
	public ArrayList<ArrayList<Result>> searchPartial(ArrayList<TreeSet<String>> queries,
			ArrayList<ArrayList<Result>> resultList) {

		lock.lockReadWrite();
		try {

			for (Entry<String, TreeMap<String, TreeSet<Integer>>> indexWord : index.entrySet()) {

				for (TreeSet<String> query : queries) {

					String queryName = String.join(" ", query);

					for (String queryWord : query) {

						Integer count = 0;
						String fileName = "";

						if (indexWord.getKey().startsWith(queryWord)) {

							String word = indexWord.getKey();

							TreeMap<String, TreeSet<Integer>> fileList = index.get(word);

							for (Entry<String, TreeSet<Integer>> fileEntry : fileList.entrySet()) {

								count = fileEntry.getValue().size();
								fileName = fileEntry.getKey();

								if (resultMap.get(queryName) == null) {

									resultMap.put(queryName, new TreeMap<>());
									resultMap.get(queryName).put(fileName, count);

								} else if (resultMap.get(queryName).get(fileName) == null) {

									resultMap.get(queryName).put(fileName, count);

								} else {

									count = count + resultMap.get(queryName).get(fileName);

									resultMap.get(queryName).put(fileName, count);

								}

							}

							count = 0;

						} else {
							resultMap.putIfAbsent(queryName, null);
						}
					}

				}

			}

			int i = 0;

			for (Entry<String, TreeMap<String, Integer>> q : resultMap.entrySet()) {

				resultList.add(new ArrayList<>());

				if (q.getValue() != null) {

					for (Entry<String, Integer> file : q.getValue().entrySet()) {

						double score = (double) file.getValue() / locations.get(file.getKey());

						Result result = new Result(file.getValue(), q.getKey(), file.getKey(), score);

						boolean copy = false;
						for (Result res : resultList.get(i)) {
							if (res.file == result.file && res.count == result.count && res.qString == result.qString) {
								copy = true;
							}
						}
						if (!copy) {
							resultList.get(i).add(result);
						}

					}

					Collections.sort(resultList.get(i));

					i++;
				} else {
					resultList.get(i).add(new Result(0, q.getKey(), null, 0));
					i++;
				}
			}

			return resultList;
		} catch (Exception e) {
			System.out.println("There was probably an error with the path");
			return null;
		} finally {
			lock.unlockReadWrite();
		}
	}

	public ArrayList<ArrayList<Result>> threadedSearch(ArrayList<TreeSet<String>> queries, boolean exact, int threads,
			InvertedIndex index, ArrayList<ArrayList<Result>> resultList) {

		WorkQueue queue = new WorkQueue(threads);

		for (TreeSet query : queries) {
			QueryTask task = new QueryTask(resultList, index, query, exact);
			queue.execute(task);

		}

		queue.finish();
		queue.shutdown();

		return resultList;
	}

	private static class QueryTask implements Runnable {

		ArrayList<ArrayList<Result>> results;
		InvertedIndex index;
		TreeSet<String> query;
		boolean exact;

		public QueryTask(ArrayList<ArrayList<Result>> results, InvertedIndex index, TreeSet<String> query,
				boolean exact) {
			this.index = index;
			this.results = results;
			this.query = query;
			this.exact = exact;
		}

		public void run() {

			System.out.println("Hello my query is: " + query.toString());

			ArrayList<TreeSet<String>> x = new ArrayList<>();
			x.add(query);

			if (exact) {
				synchronized (this) {

					index.searchExact(x, results);
				}

			} else {
				synchronized (this) {

					index.searchPartial(x, results);
				}
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
