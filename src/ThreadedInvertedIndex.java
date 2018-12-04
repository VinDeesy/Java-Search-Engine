import java.util.ArrayList;
import java.util.TreeSet;

// TODO Better class names! Use your keywords!

/**
 * Data structure to store strings and their positions.
 */
public class ThreadedInvertedIndex extends InvertedIndex {

	/**
	 * Stores a mapping of words to the positions the words were found.
	 */

	Lock lock;

	/**
	 * Initializes the index.
	 */
	public ThreadedInvertedIndex() {
		super();
		this.lock = new Lock();
	}

	/**
	 * Adds the word and the position it was found to the index.
	 *
	 * @param word     word to clean and add to index
	 * @param fileName file word is located in
	 * @return true if this index did not already contain this word and position
	 */
	public boolean add(String word, String fileName, Integer position) {

		lock.lockReadWrite();
		try {
			return super.add(word, fileName, position);
		} finally {
			lock.unlockReadWrite();
		}
	}

	public void addAll(ThreadedInvertedIndex local) {
		lock.lockReadWrite();
		try {
			super.addAll(local);
		} finally {
			lock.unlockReadWrite();
		}
	}

	/**
	 * Returns the number of times a word was found (i.e. the number of positions
	 * associated with a word in the index).
	 *
	 * @param word word to look for
	 * @return number of times the word was found
	 */
	public int count(String word) {

		lock.lockReadOnly();
		try {
			return super.count(word);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Returns the number of words stored in the index.
	 *
	 * @return number of words
	 */
	public int words() {
		lock.lockReadOnly();
		try {
			return super.words();
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word word to look for
	 * @return true if the word is stored in the index
	 */
	public boolean contains(String word) {
		lock.lockReadOnly();
		try {
			return super.contains(word);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word word to look for
	 * @param path path word is mapped to
	 * @return true if the word is stored in the index in the path
	 */
	public boolean contains(String word, String path) {

		lock.lockReadOnly();
		try {
			return super.contains(word, path);
		} finally {
			lock.unlockReadOnly();
		}
	}

	/**
	 * Tests whether the index contains the specified word.
	 *
	 * @param word     word to look for
	 * @param path     path word is mapped to
	 * @param position position in the file to look for
	 * @return true if the word is stored in the index
	 */
	public boolean contains(String word, String path, int position) {

		lock.lockReadOnly();
		try {
			return super.contains(word, path, position);
		} finally {
			lock.unlockReadOnly();
		}

	}

	// TODO Should be the same as everything else
	// TODO Use the same approach as before with lock/try/super/finally/unlock
	/**
	 * Searches the index for exact matches from a list of queries
	 *
	 * @param queries query words to search our index
	 * @return TreeMap of results
	 */

	public ArrayList<Result> searchExactThreaded(TreeSet<String> query) {

		lock.lockReadWrite();

		try {
			return super.searchExact(query);
		} finally {
			lock.unlockReadWrite();
		}

	}

	/**
	 * Searches the index for partial matches from a list of queries
	 *
	 * @param queries query words to search our index
	 * @return ArrayList of results
	 */
	public ArrayList<Result> searchPartialThreaded(TreeSet<String> query) {

		lock.lockReadWrite();

		try {
			return super.searchPartial(query);
		} finally {
			lock.unlockReadWrite();
		}

	}

	// TODO Need to make sure every public method is overridden and locked

}