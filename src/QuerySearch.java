import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class QuerySearch {
	public TreeMap<String, ArrayList<Result>> results;
	public TreeMap<String, ArrayList<Result>> map;
	public TreeMap<String, Integer> fileMap;
	public TreeMap<String, Integer> indexMap;
	// Mapping:

	public QuerySearch() {

	}

	public TreeMap<String, ArrayList<Result>> search(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index,
			ArrayList<TreeSet<String>> queries) {

		results = new TreeMap<>();

		int i = 0;
		ArrayList<Result> resultList = new ArrayList<>();
		for (TreeSet<String> q : queries) {

			String fullQuery = Arrays.toString(q.toArray(new String[q.size()]));

			for (String word : q) {

				if (index.containsKey(word)) {

					for (Entry<String, TreeSet<Integer>> file : index.get(word).entrySet()) {

						resultList.add(new Result(file.getValue().size(), fullQuery, file.getKey()));

					}
				}

			}
			if (i == 0) {
				results.putIfAbsent(fullQuery, resultList);
				i++;
			}
		}

		for (Entry<String, ArrayList<Result>> queryWord : results.entrySet()) {

			System.out.println("Size is: " + queryWord.getValue().size());

			for (Result result : queryWord.getValue()) {

				System.out.println(
						"the word is: " + result.q + " the file is: " + result.file + " the count is: " + result.count);

			}

		}

		return results;
	}

	public void condense(TreeMap<String, ArrayList<Result>> results) {

		TreeMap<String, ArrayList<Result>> resultMap = new TreeMap<>();
		TreeMap<String, Integer> fileMap = new TreeMap<>();

		for (Entry<String, ArrayList<Result>> query : results.entrySet()) {

			// Result r = new Result(0, query.getKey());

			for (Result result : query.getValue()) {

				if (!fileMap.containsKey(result.q)) {
					fileMap.put(result.q, result.count);
				} else {
					int x = fileMap.get(result.q) + result.count;
					fileMap.put(result.q, x);

				}

				// r.updateCount(fileMap.get(r.q));
			}

		}

	}
}
