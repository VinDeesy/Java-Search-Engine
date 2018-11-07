// TODO Javadoc

public class Result implements Comparable<Result> {

	public int count;

	public String file;
	public String query;
	public double score;
	private int total;

	/**
	 * Creates a new Result object and calculates the score
	 *
	 * @param count amount of occurences in the file
	 * @param q     the query
	 * @param file  file that the word is found in
	 * @param total total amount of words in the file
	 * @return number of times the word was found
	 */
	public Result(int count, String file, int total) {

		this.count = count;
		this.file = file;

		this.total = total;
		this.score = (double) this.count / this.total;
	}

	/*
	 * Creates a blank (null) Result
	 * 
	 */
	public Result() {
		this.count = 0;
		this.file = "";
		this.query = null;
		this.total = 0;
		this.score = 0;
	}

	/**
	 * Updates the count and recalculates the score when the query is found multiple
	 * times
	 * 
	 * @param count new count amount to update
	 * 
	 */
	public void updateCount(int count) {
		this.count += count;
		this.score = (double) this.count / this.total;
	}

	/**
	 * Comparator for sorting results
	 * 
	 * @param other result to compare to
	 * 
	 */

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

}