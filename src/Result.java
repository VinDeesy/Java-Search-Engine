import java.util.ArrayList;
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

}
