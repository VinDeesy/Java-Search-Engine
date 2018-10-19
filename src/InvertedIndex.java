import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Data structure to store strings and their positions.
 */
public class InvertedIndex {
	// TODO private
	/**
	 * Stores a mapping of words to the positions the words were found.
	 */
	public final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

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

	/*
	 * TODO public boolean contains(String word, String path) public boolean
	 * contains(String word, String path, int position)
	 */

	/**
	 * Returns a string representation of this index.
	 */
	@Override
	public String toString() {
		return this.index.toString();
	}

	public void toJSON(Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);) {
			TreeJSONWriter.asNestedObject(index, writer, 1);
		}
	}

	/*
	 * TODO Move this to an InvertedIndexBuilder
	 * 
	 * Create a method that gets the list of text files, loops, and calls search on
	 * all of them public static void addFiles(Path root, InvertedIndex index) {
	 * ArrayList<Path> pathList = traverser.getPaths();
	 * 
	 * for (Path path : pathList) { addFile(path, index); } }
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

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);)

		{
			Integer position = 0;
			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			String fileName = path.toString();
			String line = null;

			while ((line = reader.readLine()) != null) {

				String[] cleaned = TextParser.parse(line);

				for (String string : cleaned) {
					add(stemmer.stem(string).toString(), fileName, ++position);
				}

			}

		} catch (IOException e) {
			e.printStackTrace(); // TODO No stack traces

		}

	}

}
