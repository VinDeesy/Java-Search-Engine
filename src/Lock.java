
// TODO Eclipse System Menu --> Window --> Show View --> Tasks 

// TODO Remove old TODO comments from homework code (and fix exception handling)

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
		// TODO

		while (writers > 0) {
			try {
				this.wait();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		readers++;

	}

	/**
	 * Will decrease the number of active readers, and notify any waiting threads if
	 * necessary.
	 */
	public synchronized void unlockReadOnly() {
		// TODO

		readers--;

		if (readers == 0) {
			this.notifyAll();
		}

		/*
		 * TODO Overnotification issue. Only call notifyAll when readers is 0.
		 */
	}

	/**
	 * Will wait until there are no active readers or writers in the system, and
	 * then will increase the number of active writers.
	 * 
	 * @throws InterruptedException
	 */
	public synchronized void lockReadWrite() {
		// TODO

		while (readers > 0 || writers > 0) {
			try {
				this.wait();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		writers++;

	}

	/**
	 * Will decrease the number of active writers, and notify any waiting threads if
	 * necessary.
	 */
	public synchronized void unlockReadWrite() {
		// TODO

		--writers;

		this.notifyAll();

	}
}
