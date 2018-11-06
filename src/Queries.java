import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

//TODO Implement using streams
public class Queries {
	/**
	 * Retrieves and parses query from a text file
	 * 
	 * @param path location to text file
	 * @return list of queries
	 * 
	 */
	public static ArrayList<TreeSet<String>> getQueries(Path path) {

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {

			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

			String line = null;

			ArrayList<TreeSet<String>> queries = new ArrayList<>();

			while ((line = reader.readLine()) != null) {

				String[] cleaned = TextParser.parse(line);

				TreeSet<String> x = new TreeSet<>();

				for (String string : cleaned) {
					x.add(stemmer.stem(string).toString());
				}

				if (!queries.contains(x)) {
					queries.add(x);
				}

			}
			return queries;

		} catch (Exception e) {
			System.out.println("There was an error processing the query file");
			return null;
		}

	}

}