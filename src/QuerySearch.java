import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class QuerySearch {

	public QuerySearch() {

	}

	public ArrayList<Result> search(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index,
			ArrayList<TreeSet<String>> queries) {

		// TreeMap<String, Integer> map = new TreeMap<>();
		ArrayList<ArrayList<Result>> results = new ArrayList<>();
		double score = 0.0;
		int i = 0;

		ArrayList<Result> res = new ArrayList<>();

		boolean found = false;

		String fileName;
		Integer numOccurences = 0;
		Integer total = 0;

		HashMap<String, Integer> m = new HashMap<>();

		for (TreeSet<String> tSet : queries) {

			results.add(new ArrayList<Result>());

			Result r = new Result(0, null, tSet);

			for (String word : tSet) {

				if (index.containsKey(word)) {
					found = true;

					TreeMap<String, TreeSet<Integer>> map = index.get(word);

					for (Entry<String, TreeSet<Integer>> file : map.entrySet()) {

						if (file.getValue().size() > 0) {

							System.out.println("Current query: " + word);
							System.out.println("File is: " + file.getKey() + " Size is: " + file.getValue().size()
									+ " Num so far is: " + numOccurences);

							if (r.map.containsKey(file.getKey())) {

								// r.map.put(file.getKey(), r.map.get(file.getKey()) + file.getValue());

								numOccurences += r.map.get(file.getKey());
							} else {
								numOccurences = file.getValue().size();
							}

							r.map.put(file.getKey(), numOccurences);

						}
					}

				}

			}
			numOccurences = 0;
			if (found) {
				res.add(r);
			}
			found = false;
			i++;

		}
		Collections.sort(res);
		for (Result r : res) {
			System.out.println(r.qString);

			for (Entry<String, Integer> entry : r.map.entrySet()) {
				System.out.println("File is: " + entry.getKey() + " Num is: " + entry.getValue());
			}
		}

		return res;
	}

	public void getOutput(ArrayList<Result> results, TreeMap<String, Integer> locations) {

		for (Result r : results) {
			r.calc(locations);

		}

	}

}
