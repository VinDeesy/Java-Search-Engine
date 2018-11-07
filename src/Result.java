public class Result implements Comparable<Result> {

	public int count;

	public String file;
	public String query;
	String qString;
	double score;

	public Result(int count, String file, double score) {

		this.count = count;
		this.file = file;
		this.score = score;

	}

	public void updateCount(int count) {
		this.count = count;
	}

	public void updateScore(double score) {
		this.score = score;
	}

	@Override
	public int compareTo(Result other) {
		int c = Double.compare(other.score, this.score);

		if (c == 0) {
			c = Integer.compare(other.count, this.count);
		}
		if (c == 0) {
			c = this.file.compareTo(other.file);
		}
		return c;
	}

	public String getqString() {
		return this.qString;
	}

}