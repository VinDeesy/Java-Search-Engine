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

		InvertedIndex index = new InvertedIndex();

		if (parser.hasValue("-path")) {
			Path inputPath = Paths.get(parser.getString("-path"));

			try {
				InvertedIndexBuilder.addFiles(inputPath, index);
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

		// TODO Move this inside the if block that uses it
		Boolean exact = parser.hasFlag("-exact");

		Queries query = new Queries(index); // TODO Move this up to the top of main() method with the other definitions
		if (parser.hasValue("-search")) {

			Path queryFile = Paths.get(parser.getString("-search"));
			try {
				query.getQueries(queryFile, exact);

			} catch (Exception e) {
				System.out.println("There was an error with your query file");
			}
		}

		if (parser.hasFlag("-results")) {
			Path resultsFile;
			// TODO Use the better version of parser.getPath(..., defaultPath)
			if (parser.hasValue("-results")) {
				resultsFile = Paths.get(parser.getString("-results"));
			} else {
				resultsFile = Paths.get("results.json");
			}
			try {

				query.printSearch(resultsFile);
			} catch (Exception e) {
				System.out.println("There was an error with printing the results");
			}
		}

	}

}
