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

	public void ThreadedSearch(Path path, boolean exact, int threads) {

		WorkQueue queue = new WorkQueue(threads);
		ThreadedInvertedIndex threadedIndex = new ThreadedInvertedIndex();
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {

			String line = null;

			while ((line = reader.readLine()) != null) {

//				if (queryLine == "") {
//					continue;
//				}

				QueryTask task = new QueryTask(line, exact);
				queue.execute(task);
			}
			queue.finish();
			queue.shutdown();

		} catch (Exception e) {
			System.out.println("There was an error processing the query file");

		}

	}

	/*
	 * TODO Too many parameters... part of the issue is you need access to instance
	 * members but you have a static nested class. Remove the static keyword, then
	 * you can access those directly. String queryLine, boolean exact is all you
	 * need
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

			ArrayList<Result> resultList = null;

			if (exact) {

				resultList = index.searchExactThreaded(query, index);

			} else {
				resultList = index.searchPartialThreaded(query, index);
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
