import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class ThreadedIndexBuilder {

	public ThreadedIndexBuilder() {

	}

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

		InvertedIndexBuilder.addFile(path, index);

	}

	/**
	 * Given a root path, traverses the directory for all text files and adds the
	 * contents of the file to the index
	 *
	 * @param root  root directory to traverse
	 * @param index data structure to store words and locations
	 * @return null
	 * 
	 */
	public static void addFiles(Path root, ThreadedInvertedIndex index, int threads) throws IOException {

		WorkQueue queue = new WorkQueue(threads);

		try {
			ArrayList<Path> pathList = FileTraverser.traverse(root);

			for (Path path : pathList) {
				FileTask task = new FileTask(path, index);
				queue.execute(task);

			}
		} catch (Exception e) {
			System.out.println("Error processing file");
		} finally {
			queue.finish();
			queue.shutdown();
		}

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

		private final Path path;
		private final ThreadedInvertedIndex index;

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
