
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * This class demonstrates how to use a {@link DirectoryStream} to create a
 * recursive file listing.
 *
 * @see java.nio.file.Path
 * @see java.nio.file.Paths
 * @see java.nio.file.Files
 * @see java.nio.file.DirectoryStream
 */
public class FileTraverser {

	/**
	 * Determines whether a given file is a text file
	 *
	 * @param path file to check if is text
	 * @return true if it is a text file
	 * 
	 */

	public static boolean isTextFile(Path path) {
		String name = path.toString().toLowerCase();
		return name.endsWith(".txt") || name.endsWith(".text");
	}

	/**
	 * Traverses a directory tree, adding all text files to a list
	 *
	 * @param path file/directory
	 * @return null
	 * 
	 */

	public static void traverse(Path path, ArrayList<Path> paths) throws IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				for (Path file : listing) {
					traverse(file, paths);
				}
			}
		} else if (isTextFile(path)) {
			paths.add(path);
		}
	}

	/**
	 * Traverses a directory tree, starting at root, and adds all text files to a
	 * list
	 *
	 * @param path root directory
	 * @return list of text file paths
	 * 
	 */

	public static ArrayList<Path> traverse(Path path) throws IOException {
		ArrayList<Path> paths = new ArrayList<Path>();
		traverse(path, paths);
		return paths;
	}

}
