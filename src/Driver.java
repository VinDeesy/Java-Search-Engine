import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeMap;

public class Driver {

	/**
	 * Parses the command-line arguments to build and use an in-memory search engine
	 * from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			return;
		}
		ArgumentParser parser = new ArgumentParser();

		parser.parse(args);

		boolean threaded = false;
		int threads = 5;
		if (parser.hasFlag("-threads")) {
			threaded = true;
			threads = 5;
		}

		Path inputPath;

		InvertedIndex index = new InvertedIndex();
		boolean path = false;
		if (parser.hasValue("-path")) {
			inputPath = Paths.get(parser.getString("-path"));
			path = true;
			try {
				if (threaded) {
					InvertedIndexBuilder.addFileThreaded(inputPath, index, threads);
				} else {
					InvertedIndexBuilder.addFiles(inputPath, index);
				}
			} catch (IOException e) {
				System.out.println("There was an error building the index");
			}

		} else {
			System.out.println("No path specified, exiting...");

		}
		InvertedThreaded threadedIndex = new InvertedThreaded();
		int total = 50;
		if (parser.hasFlag("-limit")) {
			try {
				if (parser.getValue("-limit") == null) {
					total = 1;
				} else {
					total = Integer.parseInt(parser.getValue("-limit"));
				}
			} catch (NumberFormatException e) {
				System.out.println("number format exception");
				total = 50;
			}
			System.out.println("limit is: " + total);
		}
		boolean URL = false;
		if (parser.hasValue("-url")) {
			try {
				URL = true;
				URL seed = new URL(parser.getString("-url"));

				WebCrawler crawler = new WebCrawler(total, seed, threadedIndex);
				try {
					crawler.crawl(seed, total);
					index = threadedIndex;
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (MalformedURLException e) {
				System.out.println("URL Problem!");
			}
		}

		Path outputPath = null;

		if (parser.hasFlag("-index")) {

			outputPath = parser.getPath("-index", Paths.get("index.json"));
			try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);) {
				if (path) {
					index.toJSON(outputPath);
				} else {
					threadedIndex.toJSON(outputPath);
				}

			} catch (Exception e) {
				System.out.println("Error io open file: " + outputPath.toString());
			}

		}

		Path locations;
		if (parser.hasFlag("-locations")) {
			if (parser.hasValue("-locations")) {
				locations = Paths.get(parser.getString("-locations"));
			} else {
				locations = Paths.get("locations.json");
			}
			try {
				threadedIndex.locationJSON(locations);
			} catch (Exception e) {
				System.out.println("There was an error retrieving the locations file");
			}
		} else {
			System.out.println("No locations flag, not printing locations to file");
		}

		Boolean exact = parser.hasFlag("-exact");
		TreeMap<String, ArrayList<Result>> results = null;
		Queries query = new Queries(index);
		if (parser.hasValue("-search")) {

			Path queryFile = Paths.get(parser.getString("-search"));

			if (threaded) {
				query.ThreadedSearch(queryFile, exact, threads);
			} else {

				query.getQueries(queryFile, exact);
			}

			try {

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("There was an error with your query file");
			}
		}

		Path resultsFile;
		if (parser.hasFlag("-results")) {

			if (parser.hasValue("-results")) {
				resultsFile = Paths.get(parser.getString("-results"));
			} else {
				resultsFile = Paths.get("results.json");

				System.out.println("NO result path given, using results.json");
			}
			try (BufferedWriter writer = Files.newBufferedWriter(resultsFile, StandardCharsets.UTF_8);) {

				query.printSearch(writer);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Something got fuckedup with printing the results");
			}
		}

	}

}