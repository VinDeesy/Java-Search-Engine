import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class ThreadedIndexBuilder {

	// TODO Remove
	public ThreadedIndexBuilder() {
		super();
	}

	// TODO Remove
	/**
	 * Reads lines from a file, stems/cleans the line and adds the words to the
	 * index
	 *
	 * @param path  path to file
	 * @param index index to add files to
	 * @return null
	 * 
	 */
	public static void addFile(Path path, ThreadedInvertedIndex index) throws IOException {

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);)

		{
			Integer position = 0;
			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			String fileName = path.toString();
			String line = null;

			while ((line = reader.readLine()) != null) {

				String[] cleaned = TextParser.parse(line);

				for (String string : cleaned) {
					System.out.println(string);
					index.add(stemmer.stem(string).toString(), fileName, ++position);
				}

			}

		}

	}

	
	// TODO Refactor to addFiles(...)
	/**
	 * Given a root path, traverses the directory for all text files and adds the
	 * contents of the file to the index
	 *
	 * @param root  root directory to traverse
	 * @param index data structure to store words and locations
	 * @return null
	 * 
	 */
	public static void AddFiles(Path root, ThreadedInvertedIndex index, int threads) throws IOException {

		WorkQueue queue = new WorkQueue(threads);

		// TODO Move this into a try block
		ArrayList<Path> pathList = FileTraverser.traverse(root);

		for (Path path : pathList) {
			FileTask task = new FileTask(path, index);
			queue.execute(task);

		}

		// TODO Move into a finally block
		queue.finish();
		queue.shutdown();
	}

	/**
	 *
	 * Builds a local index from a file and merges it with the main index
	 *
	 * @param path  path to file
	 * @param index data structure to store words and locations
	 * @return null
	 * 
	 */
	private static class FileTask implements Runnable {
		// TODO private final
		Path path;
		ThreadedInvertedIndex index;

		public FileTask(Path path, ThreadedInvertedIndex index) {
			this.index = index;
			this.path = path;
		}

		public void run() {
			try {

				InvertedIndex local = new InvertedIndex();
				InvertedIndexBuilder.addFile(path, local);

				index.addAll(local);

			} catch (IOException e) {
				System.out.println("Error processing file");
			}
		}

	}
}
