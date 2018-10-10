import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.TreeSet;

// TODO Address warnings for your "production release"

/**
 * TODO Fill in your own comments!
 */
public class Driver {

	/*
	 * TODO Driver.main is the only method here that should never throw an
	 * exception, since it will output a stack trace to the user.
	 * 
	 * For the production release, need to output user friendly error messages
	 * instead.
	 */

	/**
	 * Parses the command-line arguments to build and use an in-memory search engine
	 * from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// TODO Never use the 1-line if statement style, always use curly braces
		if (args.length == 0) {
			return;
		}
		ArgParser parser = new ArgParser();

		parser.parse(args);

		Path inputPath;
		Boolean built = false;
		FileSearch searcher = new FileSearch();
		if (parser.hasValue("-path")) {
			inputPath = Paths.get(parser.getString("-path"));
			// System.out.println("input path is: " + inputPath.toAbsolutePath());

			FileTraverse traverser = new FileTraverse(inputPath);
			traverser.traverse(inputPath);

			ArrayList<Path> pathList = traverser.getPaths();

			for (Path path : pathList) {
				searcher.search(path);

			}
			built = true;
		} else {
			System.out.println("No path specified, exiting...");
		}

		Path outputPath;

		if (parser.hasFlag("-index")) {

			if (!parser.hasValue("-index")) {
				Files.deleteIfExists(Paths.get("index.json"));
				outputPath = Paths.get("index.json");
				Files.createFile(outputPath);
//				System.out.println(outputPath.toString());
//				System.out.println("Created?: " + Files.exists(outputPath));

			} else {
				outputPath = Paths.get(parser.getString("-index"));
			}

			try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);) {
				TreeJSONWriter.asNestedObject(searcher.index.index, writer, 1);

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
			try (BufferedWriter writer = Files.newBufferedWriter(locations, StandardCharsets.UTF_8);) {
				TreeJSONWriter.printLocations(searcher.index.locations, writer);
			}
		} else {
			System.out.println("No locations flag, not printing locations to file");
		}

		Boolean exact = parser.hasFlag("-exact");
		ArrayList<Result> results = null;
		if (parser.hasValue("-search")) {

			Path queryFile = Paths.get(parser.getString("-search"));
			System.out.println("Inputpath for queries is: " + queryFile.toString());
			ArrayList<TreeSet<String>> queries = Queries.getQueries(queryFile);

			try {
				QuerySearch qs = new QuerySearch();
				results = qs.search(searcher.index.index, queries);
				qs.getOutput(results, searcher.index.locations);
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
				Files.createFile(resultsFile);

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
