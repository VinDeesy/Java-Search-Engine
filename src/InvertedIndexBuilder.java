import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class InvertedIndexBuilder {

	public static void addFiles(Path root, InvertedIndex index) throws IOException {

		ArrayList<Path> pathList = FileTraverser.traverse(root);

		for (Path path : pathList) {
			addFile(path, index);
		}
	}

	/**
	 * Searches the file and adds the words based upon their position and file
	 * location
	 *
	 * @param path location of file to search
	 * @return null
	 * 
	 */

	public static void addFile(Path path, InvertedIndex index) {

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);)

		{
			Integer position = 0;
			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			String fileName = path.toString();
			String line = null;

			while ((line = reader.readLine()) != null) {

				String[] cleaned = TextParser.parse(line);

				for (String string : cleaned) {
					index.add(stemmer.stem(string).toString(), fileName, ++position);
				}

			}

		} catch (IOException e) {
			e.printStackTrace(); // TODO No stack traces

		}

	}

}
