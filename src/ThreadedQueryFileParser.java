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

public class ThreadedQueryFileParser implements QueryFileParserInterface {

	private final ThreadedInvertedIndex index;
	private final TreeMap<String, ArrayList<Result>> results;
	private final int threads;

	public ThreadedQueryFileParser(ThreadedInvertedIndex index, int threads) {
		this.results = new TreeMap<>();
		this.index = index;
		this.threads = threads;
	}

	/**
	 * Multithreaded search of the index
	 *
	 * @param path    path query file
	 * @param exact   exact or partial search
	 * @param threads amount of threads to create
	 * @return true if this index did not already contain this word and position
	 */
	public void getQueries(Path path, boolean exact) {

		WorkQueue queue = new WorkQueue(threads);

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {

			String line = null;

			while ((line = reader.readLine()) != null) {

				QueryTask task = new QueryTask(line, exact);
				queue.execute(task);
			}

		} catch (Exception e) {
			System.out.println("There was an error processing the query file");

		} finally {
			queue.finish();
			queue.shutdown();
		}

	}

	/**
	 * Class of Tasks to search the index
	 *
	 * @param line  query line to parse
	 * @param exact file word is located in
	 * @return none
	 */
	private class QueryTask implements Runnable {

		String line;
		boolean exact;

		public QueryTask(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		public void run() {

			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

			String[] cleaned = TextParser.parse(line);

			TreeSet<String> query = new TreeSet<>();

			for (String string : cleaned) {
				query.add(stemmer.stem(string).toString());

			}
			String queryLine = String.join(" ", query);

			synchronized (index) {
				if (results.containsKey(queryLine) || query.isEmpty()) {
					return;
				}
			}

			ArrayList<Result> resultList = null;

			if (exact) {
				resultList = index.searchExact(query);
			} else {
				resultList = index.searchPartial(query);
			}

			synchronized (index) {

				results.put(queryLine, resultList);
			}
		}

	}

	/**
	 * Prints results to JSON format
	 *
	 * @return none
	 */
	public void printSearch(Path resultsFile) throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(resultsFile, StandardCharsets.UTF_8)) {
			synchronized (index) {
				TreeJSONWriter.printSearch(results, writer);
			}
		}

	}
}
