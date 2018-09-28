import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// TODO Remove old TODO comments
// TODO Address warnings for your "production release"

// TODO In Eclipse there is an "Organize Imports" option that will remove unused imports
// TODO Can configure Eclipse to "Organize Imports" every time you save
// TODO Unused variable warnings usually mean there is cleanup needed
// TODO Always format your code before code review (can use the formatter in Eclipse)

/**
 * TODO Fill in your own comments!
 */
public class Driver {

	/*
	 * TODO
	 * Driver.main is the only method here that should never throw an exception,
	 * since it will output a stack trace to the user.
	 * 
	 * For the production release, need to output user friendly error messages instead.
	 */
	
	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// TODO Never use the 1-line if statement style, always use curly braces
		if (args.length == 0)
			return;
		
		// TODO Define your variables in the scope they are used
		Path path;
		Path output = null;
		ArgParser parser = new ArgParser();
		
		Map<String, String> map = new TreeMap<>(); 
		
		parser.parse(args);
		
		/*
		 * TODO Simplify Driver now to make it easier for future projects
		 * Also need to restructure so separate out building functionality
		 * from output functionality.
		 * 
		 * if (-path) {
		 * 		trigger building the index
		 * }
		 * 
		 * (future project flags will go here)
		 * 
		 * if (-index) {
		 * 		trigger writing the index
		 * 
		 * }
		 */
		
		Files.deleteIfExists(Paths.get("index.json"));

		  if (!parser.hasFlag("-index")) {
			  return;
		  }
		
		
		if (!parser.hasValue("-index") && parser.hasFlag("-index")) {
			System.out.println("No output path specified, creating json.txt");
			
			System.out.println("AWIDJIAWJDIAJWILDJIAWD");
			System.out.println("json was created: " + Files.createFile(Paths.get("index.json")));
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
		
		

//		System.out.println("path is: " + path.toString());
//		System.out.println("output file is: " + output.toString());
		
//		path = Paths.get("test.txt");
//		output = Paths.get("out.txt");
		
	//	Files.createDirectories(output);
		
//		
//		if (Files.exists(output))
//				System.out.println("yeah, output exists");
		
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
		//	System.out.println("tse" + pa.toString());
			searcher.search(pa);
		}
		
		
		
		}
		

		
		
	}

}
