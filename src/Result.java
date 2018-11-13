
public class Result implements Comparable<Result> {

	// TODO private, final where possible (file and total)
	public int count;
	public String file;
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
	 * @return integer c of comparison
	 * 
	 */
	@Override
	public int compareTo(Result other) {
		// TODO variable name
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