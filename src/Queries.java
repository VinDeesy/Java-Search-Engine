import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Queries {

	public static ArrayList<TreeSet<String>> getQueries(Path path) {

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {

			String line = null;
			List<String> wordList = new ArrayList<>();

			ArrayList<TreeSet<String>> queries = new ArrayList<>();
			int index = 0;

			while ((line = reader.readLine()) != null) {

				wordList = TextFileStemmer.stemLine(line);
				TreeSet<String> x = new TreeSet<>();

				x.addAll(wordList);

				if (!queries.contains(x)) {
					queries.add(x);
				}

				index++;
			}
			return queries;

		} catch (Exception e) {
			System.out.println("Uhh oh");
		}

		return null;
	}

}
