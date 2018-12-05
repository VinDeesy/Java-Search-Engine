import java.util.LinkedList;

/**
 * A custom lock that allows for multiple readers to access the object at the
 * same time; however only allows one writer in the system at a time.
 */
public class WorkQueue {

	private final PoolWorker workers[];
	private final LinkedList<Runnable> tasks;
	private volatile boolean shutdown;
	public static final int DEFAULT = 5;

	private int pending;

	/**
	 * Creates a default instance of a WorkQueue with 5 threads
	 */
	public WorkQueue() {
		this(DEFAULT);
	}

	/**
	 * Creates an instance of a WorkQueue with a given number of threads
	 * 
	 * @param threads the desired number of threads
	 */
	public WorkQueue(int threads) {
		workers = new PoolWorker[threads];
		tasks = new LinkedList<Runnable>();
		shutdown = false;
		pending = 0;

		for (int i = 0; i < threads; i++) {
			this.workers[i] = new PoolWorker();
			this.workers[i].start();
		}
	}

	/**
	 * Adds a new task (something that implements Runnable) to the end of the queue
	 * 
	 * @param r the Runnable task to be added
	 */
	public void execute(Runnable r) {
		increment();
		synchronized (tasks) {
			tasks.addLast(r);
			tasks.notifyAll();
		}
	}

	/**
	 * Increase the count of the pending variable by 1
	 */
	private synchronized void increment() {
		pending++;
	}

	/**
	 * Decreases the count of the pending variable by 1 and notifies all waiting
	 * threads when work is complete
	 */
	private synchronized void decrement() {
		pending--;

		if (pending <= 0) {
			this.notifyAll();
		}
	}

	/**
	 * Finished threads will wait here until all work is complete before shutting
	 * down
	 */
	public void finish() {
		try {
			synchronized (this) {
				while (pending > 0) {
					this.wait();
				}
			}
		} catch (Exception e) {
			System.out.println("interrupt occured while waiting to finish work");
		}
	}

	/**
	 * Shuts down all active threads in a pool
	 */
	public void shutdown() {
		shutdown = true;

		synchronized (this.tasks) {
			tasks.notifyAll();
		}
	}

	/**
	 * A single thread that handles a series of Runnable tasks from the WorkQueue
	 */
	private class PoolWorker extends Thread {
		@Override
		public void run() {
			Runnable task = null;

			while (true) {
				synchronized (tasks) {
					while (tasks.isEmpty() && !shutdown) {
						try {
							tasks.wait();
						} catch (InterruptedException e) {
							System.out.println("Thread interrupted by system");
							Thread.currentThread().interrupt();
						}
					}

					if (shutdown) {
						break;
					} else {
						task = tasks.removeFirst();
					}
				}

				try {
					task.run();
				} catch (RuntimeException e) {
					System.out.println("Error occured while running task");
				} finally {
					decrement();
				}
			}
		}
	}
}