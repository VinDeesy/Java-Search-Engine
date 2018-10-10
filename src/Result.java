import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public class Result implements Comparable<Result> {

	public int count;
	public ArrayList<String> fileName;
	public TreeMap<String, Integer> map;
	public String[] query;
	public String q;
	String qString;

	ArrayList<ResultOutput> outputs;

	public Result(int count, String fileName, TreeSet<String> set) {
		this.fileName = new ArrayList<>();
		this.count = count;
		this.query = set.toArray(new String[set.size()]);
		this.map = new TreeMap<>();
		this.qString = Arrays.toString(set.toArray(new String[set.size()]));
		this.outputs = new ArrayList<>();
	}

	public void addFile(String file) {
		this.fileName.add(file);
	}

	@Override
	public int compareTo(Result other) {
		System.out.println("JDJD");
		int c = this.qString.compareTo(other.qString);
		return c;
	}

	public String getqString() {
		return this.qString;
	}

	public void calc(TreeMap<String, Integer> locations) {

		for (Entry<String, Integer> entry : this.map.entrySet()) {
			ResultOutput resultOutput = new ResultOutput(entry.getKey(), qString, entry.getValue());
			resultOutput.calculcateScore(locations);

			outputs.add(resultOutput);

		} //

		Collections.sort(outputs);

		for (ResultOutput r : outputs) {

			System.out.println("Query is: " + r.query + " Score is: " + r.score);
		}

	}

}
