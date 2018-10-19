import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Driver {

	/**
	 * Parses the command-line arguments to build and use an in-memory search engine
	 * from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		if (args.length == 0) {
			return;
		}
		ArgumentParser parser = new ArgumentParser();

		parser.parse(args);

		Path inputPath;

		InvertedIndex index = new InvertedIndex();

		if (parser.hasValue("-path")) {
			inputPath = Paths.get(parser.getString("-path"));

			ArrayList<Path> pathList = FileTraverser.traverse(inputPath);

			for (Path path : pathList) {
				index.search(path);
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

			} else {
				outputPath = Paths.get(parser.getString("-index"));
			}

			try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);) {
				TreeJSONWriter.asNestedObject(index.index, writer, 1);

			} catch (Exception e) {
				System.out.println("Error io open file: " + outputPath.toString());
			}

		}

	}

}
