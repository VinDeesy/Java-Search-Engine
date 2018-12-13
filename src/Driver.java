import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
		ThreadedInvertedIndex threadedInvertedIndex = null;
		InvertedIndex index;
		QueryFileParserInterface queryParser;

		boolean threaded = false;
		int threads = 1;

		if (parser.hasFlag("-threads")) {
			threadedInvertedIndex = new ThreadedInvertedIndex();
			index = threadedInvertedIndex;

			threaded = true;

			try {

				threads = Integer.parseInt(parser.getValue("-threads"));
			} catch (Exception e) {
				threads = 5;
			}

			queryParser = new ThreadedQueryFileParser(threadedInvertedIndex, threads);
		} else {
			index = new InvertedIndex();
			queryParser = new QueryFileParser(index);
		}

		if (parser.hasValue("-path")) {
			Path inputPath = Paths.get(parser.getString("-path"));

			try {
				if (threaded) {
					ThreadedIndexBuilder.addFiles(inputPath, threadedInvertedIndex, threads);
				} else {
					InvertedIndexBuilder.addFiles(inputPath, index);
				}
			} catch (IOException e) {
				System.out.println("There was an error building the index");
			}

		}

		if (parser.hasFlag("-index")) {

			Path outputPath = parser.getPath("-index", Paths.get("index.json"));
			try {
				index.toJSON(outputPath);

			} catch (Exception e) {
				System.out.println("Error io open file: " + outputPath.toString());
			}

		}

		if (parser.hasFlag("-locations")) {

			Path locations = parser.getPath("-locations", Paths.get("locations.json"));
			try {
				index.locationJSON(locations);

			} catch (Exception e) {
				System.out.println("There was an error retrieving the locations file");
			}
		}

		if (parser.hasValue("-search")) {
			Boolean exact = parser.hasFlag("-exact");
			Path queryFile = Paths.get(parser.getString("-search"));
			try {

				queryParser.getQueries(queryFile, exact);

			} catch (Exception e) {
				System.out.println("There was an error with your query file");
			}
		}

		if (parser.hasFlag("-results")) {
			Path resultsFile = parser.getPath("-results", Paths.get("results.json"));

			try {
				queryParser.printSearch(resultsFile);
			} catch (Exception e) {
				System.out.println("There was an error with printing the results");
			}
		}

	}

}
