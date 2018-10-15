import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class QuerySearch {

	public TreeMap<String, TreeMap<String, Integer>> resultMap = new TreeMap<>();
	// Mapping:

	public QuerySearch() {

	}

	public ArrayList<ArrayList<Result>> searchExact(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index,
			ArrayList<TreeSet<String>> queries, TreeMap<String, Integer> locations) {

		try {

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
						resultMap.putIfAbsent(queryName, null);
					}

				}

			}

			int i = 0;

			ArrayList<ArrayList<Result>> resultList = new ArrayList<>();

			for (Entry<String, TreeMap<String, Integer>> q : resultMap.entrySet()) {

				;

				resultList.add(new ArrayList<>());

				if (q.getValue() != null) {

					for (Entry<String, Integer> file : q.getValue().entrySet()) {

						double score = (double) file.getValue() / locations.get(file.getKey());

						Result result = new Result(file.getValue(), q.getKey(), file.getKey(), score);

						resultList.get(i).add(result);

					}

					Collections.sort(resultList.get(i));

					i++;
				} else {
					resultList.get(i).add(new Result(0, q.getKey(), null, 0));
					i++;
				}
			}

			return resultList;
		} catch (Exception e) {
			System.out.println("The path was probably null");
			return null;
		}
	}

	public ArrayList<ArrayList<Result>> searchPartial(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index,
			ArrayList<TreeSet<String>> queries, TreeMap<String, Integer> locations) {

		try {

			for (Entry<String, TreeMap<String, TreeSet<Integer>>> indexWord : index.entrySet()) {

				for (TreeSet<String> query : queries) {

					String queryName = String.join(" ", query);

					for (String queryWord : query) {

						Integer count = 0;
						String fileName = "";

						if (indexWord.getKey().startsWith(queryWord)) {

							String word = indexWord.getKey();

							TreeMap<String, TreeSet<Integer>> fileList = index.get(word);

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
							resultMap.putIfAbsent(queryName, null);
						}
					}

				}

			}

			int i = 0;

			ArrayList<ArrayList<Result>> resultList = new ArrayList<>();

			for (Entry<String, TreeMap<String, Integer>> q : resultMap.entrySet()) {

				resultList.add(new ArrayList<>());

				if (q.getValue() != null) {

					for (Entry<String, Integer> file : q.getValue().entrySet()) {

						double score = (double) file.getValue() / locations.get(file.getKey());

						Result result = new Result(file.getValue(), q.getKey(), file.getKey(), score);

						resultList.get(i).add(result);

					}

					Collections.sort(resultList.get(i));

					i++;
				} else {
					resultList.get(i).add(new Result(0, q.getKey(), null, 0));
					i++;
				}
			}

			return resultList;
		} catch (Exception e) {
			System.out.println("There was probably an error with the path");
			return null;
		}
	}

}
