import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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
		FileSearch searcher = new FileSearch();
		if (parser.hasValue("-path")) {
			inputPath = Paths.get(parser.getString("-path"));
			System.out.println("input path is: " + inputPath.toAbsolutePath());

			FileTraverse traverser = new FileTraverse(inputPath);
			traverser.traverse(inputPath);

			ArrayList<Path> pathList = traverser.getPaths();

			for (Path path : pathList) {
				searcher.search(path);

			}
		} else {
			System.out.println("No path specified, exiting...");

		}

		Path outputPath;

		if (parser.hasFlag("-index")) {

			if (!parser.hasValue("-index")) {
				Files.deleteIfExists(Paths.get("index.json"));
				outputPath = Paths.get("index.json");
				Files.createFile(outputPath);
				System.out.println(outputPath.toString());
				System.out.println("Created?: " + Files.exists(outputPath));

			} else {
				outputPath = Paths.get(parser.getString("-index"));
			}

			try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);) {
				TreeJSONWriter.asNestedObject(searcher.index.index, writer, 1);

			} catch (Exception e) {
				System.out.println("Error io open file: " + outputPath.toString());
				;
			}

		}

	}

}
