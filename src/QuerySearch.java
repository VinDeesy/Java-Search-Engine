import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class QuerySearch {
	public TreeMap<String, ArrayList<Result>> results;
	public TreeMap<String, ArrayList<Result>> map;
	public TreeMap<String, Integer> fileMap;
	public TreeMap<String, Integer> indexMap;

	public TreeMap<String, TreeMap<String, Integer>> resultMap = new TreeMap<>();
	// Mapping:

	public QuerySearch() {

	}

	public TreeMap<String, ArrayList<Result>> search(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index,
			ArrayList<TreeSet<String>> queries, TreeMap<String, Integer> locations) {

		for (TreeSet<String> query : queries) {

			String queryName = String.join(" ", query);

			for (String word : query) {

				Integer count = 0;
				String fileName = "";

				TreeMap<String, TreeSet<Integer>> fileList = index.get(word);

				if (fileList != null) {

					for (Entry<String, TreeSet<Integer>> fileEntry : fileList.entrySet()) {

						count = fileEntry.getValue().size();
						fileName = fileEntry.getKey();

						if (resultMap.get(queryName) == null) {

							resultMap.put(queryName, new TreeMap<>());
							resultMap.get(queryName).put(fileName, count);

						} else if (resultMap.get(queryName).get(fileName) == null) {

							resultMap.get(queryName).put(fileName, count);

						} else {

							count = count + resultMap.get(queryName).get(fileName);

							resultMap.get(queryName).put(fileName, count);

						}

					}

					count = 0;

				} else {
					// resultMap.putIfAbsent(queryName, null);
				}

			}

		}

		int i = 0;

		ArrayList<ArrayList<Result>> resultList = new ArrayList<>();

		for (Entry<String, TreeMap<String, Integer>> q : resultMap.entrySet()) {

			System.out.println("Query is: " + q.getKey());

			resultList.add(new ArrayList<>());

			if (q.getValue() != null) {

				for (Entry<String, Integer> file : q.getValue().entrySet()) {

					System.out.println("File is: " + file.getKey() + " Count is: " + file.getValue());

					double score = (double) file.getValue() / locations.get(file.getKey());
					System.out.println("Score is: " + score);

					Result result = new Result(file.getValue(), q.getKey(), file.getKey(), score);

					resultList.get(i).add(result);

				}

				Collections.sort(resultList.get(i));
				resultList.get(i).forEach(System.out.println());

				i++;
			}
		}

		return null;

	}

}