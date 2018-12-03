import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class ThreadedIndexBuilder {

	public ThreadedIndexBuilder() {
		super();
	}

	public synchronized static void addFile(Path path, InvertedIndex index) throws IOException {

		synchronized (index) {

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

	// TODO A thread-safe inverted index, refactor name to "addFiles"
	public static void AddFiles(Path root, InvertedIndex index, int threads) throws IOException {

		WorkQueue queue = new WorkQueue(threads);

		ArrayList<Path> pathList = FileTraverser.traverse(root);

		for (Path path : pathList) {
			FileTask task = new FileTask(path, index);
			queue.execute(task);

		}

		queue.finish();
		queue.shutdown();
	}

	private static class FileTask implements Runnable {

		Path path;
		InvertedIndex index; // TODO thread-safe

		public FileTask(Path path, InvertedIndex index) {
			this.index = index;
			this.path = path;
		}

		public void run() {
			try {
				/*
				 * TODO Always slower to cause a lot of locking/unlocking Want to use local data
				 * and a single large blocking add
				 * 
				 * InvertedIndex local = new InvertedIndex(); InvertedIndexBuilder.addFile(path,
				 * local); index.addAll(local); <- create this method
				 */
				addFile(path, index);
			} catch (IOException e) {
				System.out.println("Error processing file");
			}
		}

	}
}
