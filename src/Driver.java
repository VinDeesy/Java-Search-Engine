import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;



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
		

		if (args.length == 0)
			return;

		
		ArgParser parser = new ArgParser();
		
		Map<String, String> map = new TreeMap<>(); 
		
		parser.parse(args);
		
		Files.deleteIfExists(Paths.get("index.json"));

		  if (!parser.hasFlag("-index")) {
			  return;
		  }
		
		  Path path;
		  Path output = null;
		  
		  
		if (!parser.hasValue("-index") && parser.hasFlag("-index")) {
			
			output = Paths.get("index.json");

		}
		else if (parser.getString("-index").toLowerCase().endsWith(".txt") || parser.getString("-index").toLowerCase().endsWith(".json")) {
			output = Paths.get(parser.getString("-index"));
		}
		else
			output = Paths.get(parser.getString("-index"));

		
		if (!parser.hasValue("-path")) {
			System.out.println("No path specificied, using current directory");

			 path = Paths.get(".").toAbsolutePath().normalize();
			return;
		}
		else {
			path = Paths.get(parser.getString("-path"));
		}
		
		System.out.println("yeah, output exists");
		
		FileTraverse trav = new FileTraverse(path);
		FileSearch searcher = new FileSearch(output);
		
		
		
		String lower = path.toString().toLowerCase();
			
		
		if (lower.endsWith(".txt") || lower.endsWith(".json") || lower.endsWith(".text")) {
			System.out.println("not directory");
			searcher.search(path);
		}
		
		else {
		
		
		trav.traverse(path);
		ArrayList<Path> list = trav.getPaths();
		
	
		
		for (Path pa : list) {
			searcher.search(pa);
		}
		
		
		
		}
		

		
		
	}

}
