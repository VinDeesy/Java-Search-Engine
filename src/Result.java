import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Result implements Comparable<Result> {

	public int count;
	public ArrayList<String> fileName;
	public TreeMap<String, Integer> map;
	public String file;
	public String query;
	String qString;

	ArrayList<ResultOutput> outputs;

	public Result(int count, String q, String file) {

		this.count = count;
		this.file = file;
		this.map = new TreeMap<>();

		this.outputs = new ArrayList<>();
		this.query = q;
	}

	public void addFile(String file) {
		this.fileName.add(file);
	}

	public void updateCount(int count) {
		this.count = count;
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
