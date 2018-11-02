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
	
	// TODO Remove.... when we get to the search engine, would be bad if our index had to remember every search made
	private final TreeMap<String, TreeMap<String, Integer>> resultMap;

	/**
	 * Initializes the index.
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
		this.locations = new TreeMap<>();
		this.resultMap = new TreeMap<>();
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
	 * @return ArrayList of results
	 */

	public ArrayList<ArrayList<Result>> searchExact(ArrayList<TreeSet<String>> queries) {

		try {

			for (TreeSet<String> query : queries) {

				String queryName = String.join(" ", query);

				// TODO Start here instead
				for (String word : query) {
					
					/*
					TODO
					List<Result> results = ....
					Map<String (location), Result> lookup = ...
					
					if index.containsKey(word) {
						for every location for this word
							do we already have a result for this location?
							(if lookup contains this location as a key)
							if yes, need to update the count for that result
							
							if no, need to add a new result
								Result result = new Result(...)
								results.add(result);
								lookup.put(location, result);
					
					Collections.sort(results);
					return results;
					}
					 */
					

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

			ArrayList<ArrayList<Result>> resultList = new ArrayList<>();

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
		}
	}

	/**
	 * Searches the index for partial matches from a list of queries
	 *
	 * @param queries query words to search our index
	 * @return ArrayList of results
	 */
	public ArrayList<ArrayList<Result>> searchPartial(ArrayList<TreeSet<String>> queries) {

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

			ArrayList<ArrayList<Result>> resultList = new ArrayList<>();

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
