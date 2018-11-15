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

	private final TreeMap<String, ArrayList<Result>> results; // Data structure storing search results
	private final InvertedIndex index;

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
	 * @throws IOException
	 * 
	 */
	public void getQueries(Path path, boolean exact) throws IOException {

		BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);

		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

		String line = null;

		while ((line = reader.readLine()) != null) {

			String[] cleaned = TextParser.parse(line);

			TreeSet<String> query = new TreeSet<>();

			for (String string : cleaned) {
				query.add(stemmer.stem(string).toString());

			}
			String queryLine = String.join(" ", query);

			if (queryLine.isEmpty() || results.containsKey(queryLine)) {

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

	}

	/**
	 * Retrieves and parses query from a text file
	 * 
	 * @param writer writer to write JSON
	 * @return none
	 * 
	 */
	public void printSearch(Path resultsFile) throws IOException {

		BufferedWriter writer = Files.newBufferedWriter(resultsFile, StandardCharsets.UTF_8);

		TreeJSONWriter.printSearch(results, writer);
		writer.close();
	}

}