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

	public synchronized static void addFile(Path path, InvertedIndex index) throws IOException {

		synchronized (index) {

			try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);)

			{
				Integer numWords = 0;
				Integer position = 0;
				SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
				String fileName = path.toString();
				String line = null;

				while ((line = reader.readLine()) != null) {

					String[] cleaned = TextParser.parse(line);

					for (String string : cleaned) {
						index.add(stemmer.stem(string).toString(), fileName, ++position);
					}
					numWords += cleaned.length;
					if (numWords > 0) {
						index.addLocation(path.toString(), numWords);
					}

				}

			}
		}
	}

	public static void addFileThreaded(Path root, InvertedIndex index, int threads) throws IOException {

		WorkQueue queue = new WorkQueue(threads);

		ArrayList<Path> pathList = FileTraverser.traverse(root);

		for (Path path : pathList) {
			FileTask task = new FileTask(path, index);
			queue.execute(task);

			System.out.println("THREADED!!!!");

		}

		queue.finish();
		queue.shutdown();
	}

	private static class FileTask implements Runnable {

		Path path;
		InvertedIndex index;

		public FileTask(Path path, InvertedIndex index) {
			this.index = index;
			this.path = path;
		}

		public void run() {
			try {
				addFile(path, index);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}