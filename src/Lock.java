
/**
 * A simple custom lock that allows simultaneously read operations, but
 * disallows simultaneously write and read/write operations.
 *
 * Does not implement any form or priority to read or write operations. The
 * first thread that acquires the appropriate lock should be allowed to
 * continue.
 */
public class Lock {
	private int readers;
	private int writers;

	/**
	 * Initializes a multi-reader single-writer lock.
	 */
	public Lock() {
		readers = 0;
		writers = 0;
	}

	/**
	 * Will wait until there are no active writers in the system, and then will
	 * increase the number of active readers.
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void lockReadOnly() {

		while (writers > 0) {
			try {
				this.wait();
			} catch (Exception e) {
				System.out.println("Error locking read only");
			}
		}

		readers++;

	}

	/**
	 * Will decrease the number of active readers, and notify any waiting threads if
	 * necessary.
	 */
	public synchronized void unlockReadOnly() {

		readers--;

		if (readers == 0) {
			this.notifyAll();
		}

	}

	/**
	 * Will wait until there are no active readers or writers in the system, and
	 * then will increase the number of active writers.
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void lockReadWrite() {

		while (readers > 0 || writers > 0) {
			try {
				this.wait();
			} catch (Exception e) {
				System.out.println("Error locking read/write");
			}
		}

		writers++;

	}

	/**
	 * Will decrease the number of active writers, and notify any waiting threads if
	 * necessary.
	 */
	public synchronized void unlockReadWrite() {

		--writers;

		this.notifyAll();

	}
}
