// TODO Javadoc

public class Result implements Comparable<Result> {

	// TODO private, final where appropriate
	
	public int count;

	public String file;
	public String query; // TODO Maybe remove?
	String qString; // TODO Maybe remove?
	double score;

	// TODO public Result(int count, String file, int total) {
	// TODO every time count changes, recalculate score automatically
	
	public Result(int count, String q, String file, double score) {

		this.count = count;
		this.file = file;
		this.score = score;
		this.query = q;
	}
	
	public void updateCount(int count) {
		this.count = count; // TODO this.count += count, update the score
	}

	public void updateScore(double score) { // TODO Remove
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