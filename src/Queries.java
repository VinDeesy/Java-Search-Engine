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

	public TreeMap<String, ArrayList<Result>> results;
	InvertedIndex index;

	public Queries(InvertedIndex index) {
		this.results = new TreeMap<>();
		this.index = index;
	}

	/*
	 * TODO private final TreeMap<String, List<Result>> results; private final
	 * InvertedIndex index;
	 * 
	 * public Queries(InvertedIndex index) {
	 * 
	 * }
	 * 
	 * public void getQueries(Path path, boolean exact) { loop through each line
	 * Stemmer ...
	 * 
	 * TreeSet<String> queryWords = ... String queryLine = String.join(" ", query);
	 * 
	 * if (exact) { List<Result> resultList = index.searchExact(queryWords);
	 * results.put(queryLine, resultList);
	 * 
	 * } else {
	 * 
	 * } }
	 * 
	 * public void toJSON(...) {
	 * 
	 * }
	 * 
	 */

	/**
	 * Retrieves and parses query from a text file
	 * 
	 * @param path location to text file
	 * @return list of queries
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

		} catch (Exception e) {
			System.out.println("There was an error processing the query file");

		}

	}

	public void printSearch(BufferedWriter writer) throws IOException {

		TreeJSONWriter.printSearch(results, writer);
	}

}