import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileSearch {
	wordIndex index;
	Path output;
	public FileSearch(Path output) {
		this.index = new wordIndex();
		this.output = output;
		
	}
	
	public void search(Path path) {
		
		 
		try {
		BufferedReader reader = Files.newBufferedReader(
				path, StandardCharsets.UTF_8
				);
		
		BufferedWriter writer = Files
				.newBufferedWriter(output, StandardCharsets.UTF_8);
		
		String line = null;
		String[] words;
		
		while ((line = reader.readLine()) != null) {
			line.replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+", "");
			line.toLowerCase();
			words = line.split("(?U)[^\\p{Alpha}\\p{Space}]+");
			
			
			
			List<String> stemmed = TextFileStemmer.stemLine(line);
			
			index.addAll(stemmed.toArray(new String[stemmed.size()]), path);
			
			
		}
	
		TreeJSONWriter.asNestedObject(index.index, writer, 1);
		index.position = 1;
		
		
	}catch(IOException e) {
		
	}
		
	} 
	
	public wordIndex getIndex() { return this.index;}
	
	
}
