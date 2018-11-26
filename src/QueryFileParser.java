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

public class QueryFileParser {

	private final TreeMap<String, ArrayList<Result>> results; // Data structure storing search results
	private final InvertedIndex index;

	/**
	 * Initializes the QueryFileParser
	 */
	public QueryFileParser(InvertedIndex index) {
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

	public void ThreadedSearch(Path path, boolean exact, int threads) {

		WorkQueue queue = new WorkQueue(threads);
		InvertedThreaded threadedIndex = new InvertedThreaded();
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
				ArrayList<Result> resultList = null;

				QueryTask task = new QueryTask(query, index, resultList, results, exact, queryLine, threadedIndex);
				queue.execute(task);
			}
			queue.finish();
			queue.shutdown();

		} catch (Exception e) {
			System.out.println("There was an error processing the query file");

		}

	}

	private static class QueryTask implements Runnable {

		ArrayList<Result> resultList;
		TreeSet<String> query;
		InvertedIndex index;
		public TreeMap<String, ArrayList<Result>> results;
		boolean exact;
		String queryLine;
		InvertedThreaded threadedIndex;

		public QueryTask(TreeSet<String> query, InvertedIndex index, ArrayList<Result> resultList,
				TreeMap<String, ArrayList<Result>> results, boolean exact, String queryLine,
				InvertedThreaded threadedIndex) {
			this.index = index;
			this.query = query;
			this.resultList = resultList;
			this.results = results;
			this.exact = exact;
			this.queryLine = queryLine;
			this.threadedIndex = threadedIndex;
		}

		public void run() {
			if (exact) {

				resultList = threadedIndex.searchExactThreaded(query, index);

			} else {
				resultList = threadedIndex.searchPartialThreaded(query, index);
			}
			synchronized (index) {

				results.put(queryLine, resultList);
			}
		}

	}

	public void printSearch(Path resultsFile) throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(resultsFile, StandardCharsets.UTF_8)) {

			TreeJSONWriter.printSearch(results, writer);
		}

	}

}