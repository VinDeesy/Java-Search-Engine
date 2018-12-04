import java.util.ArrayList;
import java.util.Collection;

/*
 * TODO All public methods should be overridden and locked
 */

// TODO Better class names! Use your keywords!

/**
 * Data structure to store strings and their positions.
 */
public class ThreadedInvertedIndex extends InvertedIndex {

	private final Lock lock;

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
	@Override
	public boolean add(String word, String fileName, Integer position) {

		lock.lockReadWrite();
		try {
			return super.add(word, fileName, position);
		} finally {
			lock.unlockReadWrite();
		}
	}

	/**
	 * Merges a local index into the main index
	 *
	 * @param local index to merge
	 * @return none
	 */
	@Override
	public void addAll(InvertedIndex local) {
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
	@Override
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
	@Override
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
	@Override
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
	@Override
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
	@Override
	public boolean contains(String word, String path, int position) {

		lock.lockReadOnly();
		try {
			return super.contains(word, path, position);
		} finally {
			lock.unlockReadOnly();
		}

	}

	/**
	 * Searches the index for exact matches from a list of queries
	 *
	 * @param queries query words to search our index
	 * @return TreeMap of results
	 */
	@Override
	public ArrayList<Result> searchExact(Collection<String> query) {
		// TODO Lock for read
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
	@Override
	public ArrayList<Result> searchPartial(Collection<String> query) {
		// TODO Lock for read
		lock.lockReadWrite();

		try {
			return super.searchPartial(query);
		} finally {
			lock.unlockReadWrite();
		}

	}

}