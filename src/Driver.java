import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeSet;

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
		Boolean threaded = false;
		parser.parse(args);

		int threads = 1;
		if (parser.hasFlag("-threads")) {

			threads = 5;

			if (threads < 0) {
				threads = 5;
			}
			System.out.println(threads);
			threaded = true;

		}

		Path inputPath;

		InvertedIndex index = new InvertedIndex();

		if (parser.hasValue("-path")) {
			inputPath = Paths.get(parser.getString("-path"));

			try {
				if (!threaded) {
					InvertedIndexBuilder.addFiles(inputPath, index);
				} else {
					InvertedIndexBuilder.addFileThreaded(inputPath, index, threads);
				}
			} catch (IOException e) {
				System.out.println("There was an error building the index");
			}

		} else {
			System.out.println("No path specified, exiting...");

		}

		Path outputPath = null;

		if (parser.hasFlag("-index")) {

			outputPath = parser.getPath("-index", Paths.get("index.json"));
			try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);) {

				index.toJSON(outputPath);

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
				index.locationJSON(locations);
			} catch (Exception e) {
				System.out.println("There was an error retrieving the locations file");
			}
		} else {
			System.out.println("No locations flag, not printing locations to file");
		}

		Boolean exact = parser.hasFlag("-exact");
		ArrayList<ArrayList<Result>> results = null;

		if (parser.hasValue("-search")) {

			Path queryFile = Paths.get(parser.getString("-search"));
			ArrayList<TreeSet<String>> queries = Queries.getQueries(queryFile);

			if (threaded) {
				results = InvertedThreaded.threadedSearch(queries, exact, threads, index);
				System.out.println(results.toString());
			} else {

				try {
					if (exact) {
						results = index.searchExact(queries);
					} else {
						results = index.searchPartial(queries);
					}

				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("There was an error with your query file");
				}
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

				TreeJSONWriter.printSearch(results, writer);
			} catch (Exception e) {

				System.out.println("Something got fuckedup with printing the results");
			}
		}

	}

}
