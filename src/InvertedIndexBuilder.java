import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class InvertedIndexBuilder {

	/**
	 * Given a root path, traverses the directory for all text files and adds the
	 * contents of the file to the index
	 *
	 * @param root  root directory to traverse
	 * @param index data structure to store words and locations
	 * @return null
	 * 
	 */

	public static void addFiles(Path root, InvertedIndex index) throws IOException {

		ArrayList<Path> pathList = FileTraverser.traverse(root);

		for (Path path : pathList) {
			addFile(path, index);
		}
	}

	/**
	 * Given a file, adds all words and locations to the index
	 *
	 * @param path  location of file to search
	 * @param index data structure to store words and locations
	 * @return null
	 * @throws IOException
	 * 
	 */

	public static void addFile(Path path, InvertedIndex index) throws IOException {

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

		}

	}
}