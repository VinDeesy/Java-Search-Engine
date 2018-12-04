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

public class ThreadedQueryFileParser {

	ThreadedInvertedIndex index;
	private final TreeMap<String, ArrayList<Result>> results;

	public ThreadedQueryFileParser(ThreadedInvertedIndex index) {
		this.results = new TreeMap<>();
		this.index = index;
	}

	/**
	 * Multithreaded search of the index
	 *
	 * @param path    path query file
	 * @param exact   exact or partial search
	 * @param threads amount of threads to create
	 * @return true if this index did not already contain this word and position
	 */
	public void ThreadedSearch(Path path, boolean exact, int threads) {

		WorkQueue queue = new WorkQueue(threads);

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {

			String line = null;

			while ((line = reader.readLine()) != null) {

				QueryTask task = new QueryTask(line, exact);
				queue.execute(task);
			}
			queue.finish();
			queue.shutdown();

		} catch (Exception e) {
			System.out.println("There was an error processing the query file");

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

			if (queryLine == "") {
				return;
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

			TreeJSONWriter.printSearch(results, writer);
		}

	}
}
