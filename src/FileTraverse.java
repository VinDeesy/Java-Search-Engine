
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
public class FileTraverse {
	
	private final ArrayList<Path> fileList;
	private final Path path;
	
	public FileTraverse(Path path) {
		this.path = path;
		this.fileList = new ArrayList<>();
	}
	
	/**
	 * Outputs the name of the file or subdirectory, with proper indentation to
	 * help indicate the hierarchy. If a subdirectory is encountered, will
	 * recursively list all the files in that subdirectory.
	 *
	 * The recursive version of this method is private. Users of this class will
	 * have to use the public version (see below).
	 *
	 * @param prefix the padding or prefix to put infront of the file or
	 *               subdirectory name
	 * @param path   to retrieve the listing, assumes a directory and not a file
	 *               is passed
	 * @throws IOException
	 */
	private void traverse(String prefix, Path path) throws IOException {
		/*
		 * The try-with-resources block makes sure we close the directory stream
		 * when done, to make sure there aren't any issues later when accessing this
		 * directory.
		 *
		 * Note, however, we are still not catching any exceptions. This type of try
		 * block does not have to be accompanied with a catch block. (You should,
		 * however, do something about the exception.)
		 */
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			// Efficiently iterate through the files and subdirectories.
			for (Path file : listing) {
				// Print the name with the proper padding/prefix.
				//System.out.print(prefix + file.getFileName());

				// Check if this is a subdirectory
				if (Files.isDirectory(file)) {
					// Add a slash so we can tell it is a directory
				//	System.out.println("/");

					
					
					
					// Recursively traverse the subdirectory.
					// Add a little bit of padding so files in subdirectory
					// are indented under that directory.
					traverse("  " + prefix, file);
				} else {
					// Add the file size next to the name
					if (file.toString().toLowerCase().endsWith(".txt") || file.toString().toLowerCase().endsWith(".text")) {
						fileList.add(file);
					}
					
					
				//	System.out.printf(" (%d bytes)%n", Files.size(file));
				}
			}
		}
	}

	/**
	 * Safely starts the recursive traversal with the proper padding. Users of
	 * this class can access this method, so some validation is required.
	 *
	 * @param directory to traverse
	 * @throws IOException
	 */
	public void traverse(Path directory) throws IOException {
		if (Files.isDirectory(directory)) {
			traverse("- ", directory);
		} else if (Files.exists(directory)) {
				fileList.add(directory);
			}
				else {
					System.out.println("The Path: " + directory.toString() + " does not exist");
				}
		}
	
	
	public ArrayList<Path> getPaths() { return fileList; }

}
