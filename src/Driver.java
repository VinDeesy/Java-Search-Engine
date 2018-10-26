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
	public static void main(String[] args) throws IOException { // TODO Remove the "throws" here!
//
		if (args.length == 0) {
			return;
		}
		ArgumentParser parser = new ArgumentParser();

		parser.parse(args);

		Path inputPath;

		InvertedIndex index = new InvertedIndex();

		if (parser.hasValue("-path")) {
			inputPath = Paths.get(parser.getString("-path"));

			InvertedIndexBuilder.addFiles(inputPath, index);

		} else {
			System.out.println("No path specified, exiting...");

		}

		Path outputPath;

		if (parser.hasFlag("-index")) {

			// TODO More complicated than you need...
			// TODO No need to delete or create... just need:
			// TODO outputPath = parser.getPath("-index", Paths.get("index.json"));

			if (!parser.hasValue("-index")) {
				Files.deleteIfExists(Paths.get("index.json"));
				outputPath = Paths.get("index.json");
				Files.createFile(outputPath);

			} else {
				outputPath = Paths.get(parser.getString("-index"));
			}

			try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);) {

				index.toJSON(outputPath);

			} catch (Exception e) {
				System.out.println("Error io open file: " + outputPath.toString());
			}

		}

	}

}
