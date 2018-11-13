import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class Queries {

	// TODO private, final where possible
	
	public TreeMap<String, ArrayList<Result>> results; // Data structure storing search results
	InvertedIndex index;

	/**
	 * Initializes the Queries
	 */
	public Queries(InvertedIndex index) {
		this.results = new TreeMap<>();
		this.index = index;
	}

	/**
	 * Retrieves and parses query from a text file
	 * 
	 * @param path  location to text file
	 * @param exact partial or exact search
	 * @return none
	 * 
	 */
	public void getQueries(Path path, boolean exact) {

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {

			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

			String line = null;

			while ((line = reader.readLine()) != null) {

				String[] cleaned = TextParser.parse(line);

				TreeSet<String> query = new TreeSet<>();

				for (String string : cleaned) {
					query.add(stemmer.stem(string).toString());

				}
				String queryLine = String.join(" ", query);
				
				// TODO if (queryLine.isEmpty() || results.containsKey(queryLine)) {
				if (queryLine == "") {
					continue;
				}
				ArrayList<Result> resultList;
				if (exact) {

					resultList = index.searchExact(query);

				} else {
					resultList = index.searchPartial(query);
				}
				results.put(queryLine, resultList);

			}

		} catch (Exception e) { // TODO Remove the catch, throw the exception to Driver
			System.out.println("There was an error processing the query file");

		}

	}

	// TODO Change the parameter to a Path and create the writer in the method
	// TODO Simliar to your write methods in your inverted index
	/**
	 * Retrieves and parses query from a text file
	 * 
	 * @param writer writer to write JSON
	 * @return none
	 * 
	 */
	public void printSearch(BufferedWriter writer) throws IOException {

		TreeJSONWriter.printSearch(results, writer);
	}

}