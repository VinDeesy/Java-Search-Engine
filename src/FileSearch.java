import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;

public class FileSearch {
	wordIndex index;
	Path output;
	TreeMap<String, Integer> locations;
	int numWords;
	
	public FileSearch(Path output) {
		this.index = new wordIndex();
		this.output = output;
		locations = new TreeMap<>();
		this.numWords = 0;
	}
	 
	public void search(Path path) {
		
		 
		try ( BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
				BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8);)
		{

			
			
			
		String line = null;
		String[] words;
		
		while ((line = reader.readLine()) != null) {
			line.replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+", "");
			line.toLowerCase();
			words = line.split("(?U)[^\\p{Alpha}\\p{Space}]+");
			
			
			
			List<String> stemmed = TextFileStemmer.stemLine(line);
			numWords += stemmed.size();
			index.addAll(stemmed.toArray(new String[stemmed.size()]), path);
			
			
		}
		System.out.println("num words is: " + numWords);
		locations.put(path.toString(), numWords);
		TreeJSONWriter.asNestedObject(index.index, writer, 1);
		index.position = 1;
		
		
	}catch(IOException e) {
		e.toString();
	}
		
	} 
	
	public wordIndex getIndex() { return this.index; }
	
	
}
