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

	public static void AddFiles(Path root, ThreadedInvertedIndex index, int threads) throws IOException {

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
		ThreadedInvertedIndex index;

		public FileTask(Path path, ThreadedInvertedIndex index) {
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

				InvertedIndex local = new InvertedIndex();
				InvertedIndexBuilder.addFile(path, local);

				index.addAll(local);

			} catch (IOException e) {
				System.out.println("Error processing file");
			}
		}

	}
}
