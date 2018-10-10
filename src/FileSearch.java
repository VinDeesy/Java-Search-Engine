import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileSearch {
	wordIndex index;
	Integer numWords;

	public FileSearch() {
		this.index = new wordIndex();
		this.numWords = 0;

	}

	public void search(Path path) {

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);)

		{

			String line = null;
			String[] words = null;

			while ((line = reader.readLine()) != null) {

				line.replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+", "");
				line.toLowerCase();
				words = line.split("(?U)[^\\p{Alpha}\\p{Space}]+");

				List<String> stemmed = TextFileStemmer.stemLine(line);

				index.addAll(stemmed.toArray(new String[stemmed.size()]), path);

				numWords += stemmed.size();
			}
			index.position = 1;
			if (numWords > 0) {
				index.locations.put(path.toString(), numWords);
			}

			numWords = 0;
		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	public wordIndex getIndex() {
		return this.index;
	}

}
