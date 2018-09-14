import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * TODO Fill in your own comments!
 */
public class Driver {

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @return 0 if everything went well
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	
		Path p = Paths.get("/opt/dir");
		ArgParser parser = new ArgParser();
		parser.parse(args);
		
		
		
//		if (!parser.hasFlag("-path")) {
//			System.out.println("No path specificied, using current directory");
//			Path path = Paths.get(".").toAbsolutePath().normalize();
//		}
//		else {
//			p = Paths.get(parser.getString("-path")).toAbsolutePath().normalize();
//		}
		
		FileTraverse trav = new FileTraverse(p);
		trav.traverse(p);
		ArrayList<Path> list = trav.getPaths();

		FileSearch searcher = new FileSearch();

		for (Path pa : list) {
			System.out.println(pa.toString());
			searcher.search(pa);
		}
		
		
		
		
		
		
		
	}

}
