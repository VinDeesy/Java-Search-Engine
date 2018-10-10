import java.util.TreeMap;

public class ResultOutput implements Comparable<ResultOutput> {
	String query;
	double score;
	String where;
	double count;

	public ResultOutput(String where, String query, int count) {
		this.where = where;
		this.count = count;
		this.query = query;
	}

	public void calculcateScore(TreeMap<String, Integer> locations) {

		score = count / locations.get(where);

	}

	@Override
	public int compareTo(ResultOutput other) {

		int c = Double.compare(other.score, this.score);

		if (c == 0) {
			c = Double.compare(other.count, this.count);
		}

		if (c == 0) {
			c = String.CASE_INSENSITIVE_ORDER.compare(this.where, other.where);
		}

		return c;
	}

}
