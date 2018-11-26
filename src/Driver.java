import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

		boolean threaded = false;
		int threads = 5;
		if (parser.hasFlag("-threads")) {
			threaded = true;

			if (parser.hasValue("-threads")) {

				System.out.println(parser.getString("-threads"));
				threads = Integer.parseInt(parser.getValue("-threads"));
			}
		}

		InvertedIndex index = new InvertedIndex();
		QueryFileParser query = new QueryFileParser(index);
		if (parser.hasValue("-path")) {
			Path inputPath = Paths.get(parser.getString("-path"));

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

		if (parser.hasFlag("-index")) {

			Path outputPath = parser.getPath("-index", Paths.get("index.json"));
			try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);) {

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

		QueryFileParser query = new QueryFileParser(index);



		if (parser.hasValue("-search")) {
			Boolean exact = parser.hasFlag("-exact");
			Path queryFile = Paths.get(parser.getString("-search"));
			try {

				if (threaded) {
					query.ThreadedSearch(queryFile, exact, threads);
				} else {

					query.getQueries(queryFile, exact);
				}


			} catch (Exception e) {
				System.out.println("There was an error with your query file");
			}
		}

		if (parser.hasFlag("-results")) {
			Path resultsFile = parser.getPath("-results", Paths.get("results.json"));

			try {

				query.printSearch(resultsFile);
			} catch (Exception e) {
				System.out.println("There was an error with printing the results");
			}
		}

	}

}