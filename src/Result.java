
public class Result implements Comparable<Result> {

	private int count;
	private final String file;
	private double score;
	private final int total;

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

		int compare = Double.compare(other.score, this.score);

		if (compare == 0) {
			compare = Integer.compare(other.count, this.count);
		}

		if (compare == 0) {
			compare = this.file.compareTo(other.file);
		}

		return compare;
	}

	/**
	 * Getter for result count
	 * 
	 * @param none
	 * @return count of occurrences
	 * 
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Getter for result score
	 * 
	 * @param none
	 * @return score of result
	 * 
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Getter for file
	 * 
	 * @param none
	 * @return file of result
	 * 
	 */
	public String getFile() {
		return file;
	}

}