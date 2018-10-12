import java.util.ArrayList;

public class Result implements Comparable<Result> {

	public int count;

	public String file;
	public String query;
	String qString;
	double score;

	ArrayList<ResultOutput> outputs;

	public Result(int count, String q, String file, double score) {

		this.count = count;
		this.file = file;
		this.score = score;
		this.query = q;
	}

	public void updateCount(int count) {
		this.count = count;
	}

	public void updateScore(double score) {
		this.score = score;
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
